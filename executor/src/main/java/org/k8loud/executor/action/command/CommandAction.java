package org.k8loud.executor.action.command;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

@Slf4j
public abstract class CommandAction extends Action {
    protected CommandExecutionService commandExecutionService;
    protected String host;
    protected Integer port;
    protected String privateKey;
    protected String user;

    protected CommandAction(Params params, CommandExecutionService commandExecutionService) throws ActionException {
        super(params);
        this.commandExecutionService = commandExecutionService;
    }

    @Override
    public void unpackParams(Params params) {
        this.host = params.getRequiredParam("host");
        this.port = Integer.parseInt(params.getOptionalParam("port", "22"));
        this.privateKey = params.getRequiredParam("privateKey");
        this.user = params.getRequiredParam("user");
        unpackAdditionalParams(params);
    }

    protected abstract void unpackAdditionalParams(Params params);

    @Override
    public ExecutionRS perform() {
        String result;
        try {
            result = performCommandAction();
        } catch (CommandException e) {
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }
        log.info("Result: {}", result);
        return ExecutionRS.builder()
                .result(result)
                .exitCode(ExecutionExitCode.OK)
                .build();
    }

    protected String performCommandAction() throws CommandException {
        return commandExecutionService.executeCommand(host, port, privateKey, user, buildCommand());
    }

    protected abstract String buildCommand();
}
