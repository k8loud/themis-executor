package org.k8loud.executor.actions.command;

import data.Params;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;

public class CustomScriptAction extends CommandAction {
    private String command;

    public CustomScriptAction(Params params, CommandExecutionService commandExecutionService) throws ActionException {
        super(params, commandExecutionService);
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
