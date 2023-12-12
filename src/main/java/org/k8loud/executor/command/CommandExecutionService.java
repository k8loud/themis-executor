package org.k8loud.executor.command;

import org.k8loud.executor.exception.CommandException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

public interface CommandExecutionService {
    Map<String, Object> executeCommand(String host, Integer port, String privateKey, String user, String command)
            throws CommandException, ValidationException;
}
