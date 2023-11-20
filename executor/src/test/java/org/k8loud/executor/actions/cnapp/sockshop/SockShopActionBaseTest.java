package org.k8loud.executor.actions.cnapp.sockshop;

import org.k8loud.executor.actions.ActionBaseTest;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.mockito.Mock;

public abstract class SockShopActionBaseTest extends ActionBaseTest {
    protected static final String APPLICATION_URL_KEY = "applicationUrl";
    protected static final String APPLICATION_URL = "http://localhost:8082";

    @Mock
    protected SockShopService sockShopServiceMock;
}
