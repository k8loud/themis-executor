package org.k8loud.executor.actions.cnapp.sockshop;

import org.k8loud.executor.actions.ActionBaseTest;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.mockito.Mock;

public abstract class SockShopActionBaseTest extends ActionBaseTest {
    protected static final String APPLICATION_URL_KEY = "applicationUrl";
    protected static final String APPLICATION_URL = "http://localhost:8082";
    protected static final String USERNAME_KEY = "username";
    protected static final String USERNAME = "user994";
    protected static final String PASSWORD_KEY = "password";
    protected static final String PASSWORD = "pass994";
    protected static final String ID = "655b6dfacb8de600019db115";

    @Mock
    protected SockShopService sockShopServiceMock;
}
