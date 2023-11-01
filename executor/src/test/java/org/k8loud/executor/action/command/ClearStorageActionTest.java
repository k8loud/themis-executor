package org.k8loud.executor.action.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CommandException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

class ClearStorageActionTest extends CommandActionBaseTest {
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, "192.168.13.37",
            PORT_KEY, "22",
            PRIVATE_KEY_KEY, "p4009jZSD+16k8xk",
            USER_KEY, "ubuntu",
            "paths", "/home/ubuntu /dev/null",
            "regexPattern", "to_delete*",
            "dateFrom", "2022-01-05T10:25:33",
            "dateTo", "2024-01-05T10:25:33"
    ));
    ClearStorageAction clearStorageAction;

    @BeforeEach
    public void setUp() throws ActionException {
        clearStorageAction = new ClearStorageAction(PARAMS, commandExecutionServiceMock);
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // given
        final String host = PARAMS.getRequiredParam(HOST_KEY);
        final Integer port = Integer.parseInt(PARAMS.getRequiredParam(PORT_KEY));
        final String privateKey = PARAMS.getRequiredParam(PRIVATE_KEY_KEY);
        final String user = PARAMS.getRequiredParam(USER_KEY);
        final String command = "find /home/ubuntu /dev/null -name 'to_delete*' -newermt 2022-01-05T10:25:33 ! " +
                "-newermt 2024-01-05T10:25:33 -depth -exec rm -rf {} \\;";

        // when
        clearStorageAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(host), eq(port), eq(privateKey), eq(user), eq(command));
    }
}
