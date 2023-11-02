package org.k8loud.executor.action.command;

import org.k8loud.executor.action.ActionBaseTest;
import org.k8loud.executor.command.CommandExecutionService;
import org.mockito.Mock;

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
}
