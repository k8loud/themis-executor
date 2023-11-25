package org.k8loud.executor.action.command;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;

public class CustomScriptAction extends CommandAction {
    private String command;

    public CustomScriptAction(Params params, CommandExecutionService commandExecutionService) throws ActionException {
        super(params, commandExecutionService);
    }

    @Builder
    public CustomScriptAction(CommandExecutionService commandExecutionService, String host, Integer port,
                              String privateKey, String user,
                              String command) {
        super(commandExecutionService, host, port, privateKey, user);
        this.command = command;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        command = params.getRequiredParam("command");
    }

    @Override
    protected String buildCommand() {
        return command;
    }
}
