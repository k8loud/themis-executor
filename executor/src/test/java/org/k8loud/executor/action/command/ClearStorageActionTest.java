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
    private static final String PATHS = "/home/ubuntu /dev/null";
    private static final String REGEX_PATTERN = "to_delete*";
    private static final String DATE_FROM = "2022-01-05T10:25:33";
    private static final String DATE_TO = "2024-01-05T10:25:33";
    private static final Params PARAMS = new Params(Map.of(
            HOST_KEY, HOST,
            PORT_KEY, PORT,
            PRIVATE_KEY_KEY, PRIVATE_KEY,
            USER_KEY, USER,
            "paths", PATHS,
            "regexPattern", REGEX_PATTERN,
            "dateFrom", DATE_FROM,
            "dateTo", DATE_TO
    ));
    ClearStorageAction clearStorageAction;

    @BeforeEach
    public void setUp() throws ActionException {
        clearStorageAction = new ClearStorageAction(PARAMS, commandExecutionServiceMock);
    }

    @Test
    void testCommandExecutionServiceIsCalled() throws CommandException {
        // given
        final String command = String.format("find %s -name '%s' -newermt %s ! -newermt %s -depth -exec rm -rf {} \\;",
                PATHS, REGEX_PATTERN, DATE_FROM, DATE_TO);

        // when
        clearStorageAction.execute();

        // then
        verify(commandExecutionServiceMock).executeCommand(eq(HOST), eq(Integer.parseInt(PORT)), eq(PRIVATE_KEY),
                eq(USER), eq(command));
    }
}
