package org.k8loud.executor.action.command;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import static org.k8loud.executor.exception.code.CommandExceptionCode.COMMAND_IS_MISSING;

@Setter
public abstract class CommandAction extends Action {
    protected CommandExecutionService commandExecutionService;
    protected String host;
    protected Integer port;
    protected String privateKey;
    protected String user;
    @Nullable
    protected String command;

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
        this.command = params.getOptionalParam("command", null);
        unpackAdditionalParams(params);
    }

    protected abstract void unpackAdditionalParams(Params params);

    @Override
    public ExecutionRS perform() {
        try {
            performCommandAction();
        } catch (CommandException e) {
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }

        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }

    protected abstract void performCommandAction() throws CommandException;

    protected void delegateCommandExecution() throws CommandException {
        if (this.command != null) {
            delegateCommandExecution(this.command);
        } else {
            throw new CommandException(COMMAND_IS_MISSING);
        }
    }

    protected void delegateCommandExecution(String command) throws CommandException {
        commandExecutionService.executeCommand(host, port, privateKey, user, command);
    }
}
