package org.k8loud.executor.actions.command;

import org.junit.jupiter.api.BeforeEach;
import org.k8loud.executor.actions.ActionBaseTest;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.CommandException;
import org.k8loud.executor.exception.ValidationException;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public abstract class CommandActionBaseTest extends ActionBaseTest {
    protected final static String HOST_KEY = "host";
    protected final static String PORT_KEY = "port";
    protected final static String PRIVATE_KEY_KEY = "privateKey";
    protected final static String USER_KEY = "user";
    protected static final String HOST = "192.168.13.37";
    protected static final String PORT = "22";
    protected static final String PRIVATE_KEY = "p4009jZSD+16k8xk";
    protected static final String USER = "ubuntu";

    @Mock
    protected CommandExecutionService commandExecutionServiceMock;

    @BeforeEach
    protected void setUp() throws CommandException, ValidationException {
        when(commandExecutionServiceMock.executeCommand(anyString(), anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(resultMap);
    }
}
