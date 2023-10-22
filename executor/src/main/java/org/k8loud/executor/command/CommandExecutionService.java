package org.k8loud.executor.command;

import org.k8loud.executor.exception.CommandException;

public interface CommandExecutionService {
    String executeCommand(String host, Integer port, String privateKey, String user, String command)
            throws CommandException;
}
