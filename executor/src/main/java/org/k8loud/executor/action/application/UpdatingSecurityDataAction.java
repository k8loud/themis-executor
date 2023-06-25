package org.k8loud.executor.action.application;

import data.ExecutionExitCode;
import data.ExecutionRS;
import org.k8loud.executor.action.Action;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class UpdatingSecurityDataAction extends Action {
    private String sourceIP;
    private int listenerPort;
    private String resourceName;
    // params ----------------------------------------------------------------------------------------------------------
    private static final int BUFFER_SIZE = 4096;
    private Socket listenerSocket;
    private DataOutputStream out;
    private DataInputStream in;

    public UpdatingSecurityDataAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void unpackParams(Map<String, String> params) {
        sourceIP = params.get("sourceIP");
        listenerPort = Integer.parseInt(params.get("listenerPort"));
        resourceName = params.get("resourceName");
    }

    @Override
    public ExecutionRS perform() {
        initConnection();
        ExecutionRS response = sendResource();
        finalizeConnection();
        return response;
    }

    private void initConnection() {
        try {
            Socket listenerSocket = new Socket(sourceIP, listenerPort);
            PrintWriter out = new PrintWriter(listenerSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(listenerSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ExecutionRS sendResource() {
        int count;
        byte[] buffer = new byte[BUFFER_SIZE];
        while (true)
        {
            try {
                if ((count = in.read(buffer)) <= 0) break;
                out.write(buffer, 0, count);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            out.write("FINISHED".getBytes());
            in.read(buffer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String response = new String(buffer, StandardCharsets.UTF_8);
        ExecutionExitCode exitCode = response.equals("SUCCESS") ? ExecutionExitCode.OK : ExecutionExitCode.NOT_OK;
        return ExecutionRS.builder().result(response).exitCode(exitCode).build();
    }

    private void finalizeConnection() {
        try {
            out.close();
            in.close();
            listenerSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
