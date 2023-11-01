package org.k8loud.executor.action.command;

import org.k8loud.executor.action.ActionBaseTest;
import org.k8loud.executor.command.CommandExecutionService;
import org.mockito.Mock;

public abstract class CommandActionBaseTest extends ActionBaseTest {
    protected final static String HOST_KEY = "host";
    protected final static String PORT_KEY = "port";
    protected final static String PRIVATE_KEY_KEY = "privateKey";
    protected final static String USER_KEY = "user";

    @Mock
    protected CommandExecutionService commandExecutionServiceMock;
}
