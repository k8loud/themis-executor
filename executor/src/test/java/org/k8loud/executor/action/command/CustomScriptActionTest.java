package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class CustomScriptActionTest extends CommandActionBaseTest {
    private static final String COMMAND_KEY = "command";
    private static final String COMMAND = "echo hello";
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, "192.168.13.37",
            PORT_KEY, "22",
            PRIVATE_KEY_KEY, "p4009jZSD+16k8xk",
            USER_KEY, "ubuntu",
            COMMAND_KEY, COMMAND
    ));
    CustomScriptAction customScriptAction;

    @BeforeEach
    public void setUp() throws ActionException {
        customScriptAction = new CustomScriptAction(PARAMS, commandExecutionServiceMock);
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // given
        final String host = PARAMS.getRequiredParam(HOST_KEY);
        final Integer port = Integer.parseInt(PARAMS.getRequiredParam(PORT_KEY));
        final String privateKey = PARAMS.getRequiredParam(PRIVATE_KEY_KEY);
        final String user = PARAMS.getRequiredParam(USER_KEY);

        // when
        customScriptAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(host), eq(port), eq(privateKey), eq(user), eq(COMMAND));
    }
}
