package org.k8loud.executor.actions.command;

import data.Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ClearStorageActionTest {
    private static final Params PARAMS = new Params(Map.of(
            "host", "192.168.13.37",
            "port", "22",
            "privateKey", "p4009jZSD+16k8xk",
            "user", "ubuntu",
            "paths", "/home/ubuntu /dev/null",
            "regexPattern", "to_delete*",
            "dateFrom", "2022-01-05T10:25:33",
            "dateTo", "2024-01-05T10:25:33"
    ));
    @Mock
    CommandExecutionService commandExecutionService;
    ClearStorageAction clearStorageAction;

    @BeforeEach
    public void setUp() throws ActionException {
        clearStorageAction = new ClearStorageAction(PARAMS, commandExecutionService);
    }

    @Test
    void testBuildCommand() {
        // when
        String builtCommand = clearStorageAction.buildCommand();

        // then
        assertEquals("find /home/ubuntu /dev/null -name 'to_delete*' -newermt 2022-01-05T10:25:33 ! -newermt " +
                        "2024-01-05T10:25:33 -depth -exec rm -rf {} \\;", builtCommand);
    }
}
