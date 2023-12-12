package org.k8loud.executor.actions.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.Params;

import java.util.Map;

@Slf4j
@AllArgsConstructor
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
    protected Map<String, Object> executeBody() throws CommandException, ValidationException {
        return commandExecutionService.executeCommand(host, port, privateKey, user, buildCommand());
    }

    protected abstract String buildCommand();
}
