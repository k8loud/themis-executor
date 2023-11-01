package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class CommandActionTest extends CommandActionBaseTest {
    private static final String COMMAND = "echo hello";
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, "192.168.13.37",
            PORT_KEY, "22",
            PRIVATE_KEY_KEY, "p4009jZSD+16k8xk",
            USER_KEY, "ubuntu"
    ));
    CommandAction commandAction;

    @BeforeEach
    public void setUp() throws ActionException {
        commandAction = new CommandAction(PARAMS, commandExecutionServiceMock) {
            @Override
            protected void unpackAdditionalParams(Params params) {

            }

            @Override
            protected String buildCommand() {
                return COMMAND;
            }
        };
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // given
        final String host = PARAMS.getRequiredParam(HOST_KEY);
        final Integer port = Integer.parseInt(PARAMS.getRequiredParam(PORT_KEY));
        final String privateKey = PARAMS.getRequiredParam(PRIVATE_KEY_KEY);
        final String user = PARAMS.getRequiredParam(USER_KEY);

        // when
        commandAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(host), eq(port), eq(privateKey), eq(user), eq(COMMAND));
    }
}
