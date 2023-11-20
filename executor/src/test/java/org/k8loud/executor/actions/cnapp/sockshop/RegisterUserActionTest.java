package org.k8loud.executor.actions.cnapp.sockshop;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CNAppException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterUserActionTest extends SockShopActionBaseTest {
    private static final String USERNAME = "userVal";
    private static final String PASSWORD = "passVal";
    private static final String EMAIL = "em@il.com";
    private static final Params PARAMS = new Params(Map.of(
            APPLICATION_URL_KEY, APPLICATION_URL,
            "username", USERNAME,
            "password", PASSWORD,
            "email", EMAIL
    ));

    @Test
    void testValidParams() throws CNAppException, ActionException {
        // given
        RegisterUserAction registerUserAction = new RegisterUserAction(PARAMS, sockShopServiceMock);
        when(sockShopServiceMock.registerUser(anyString(), anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = registerUserAction.execute();

        // then
        verify(sockShopServiceMock).registerUser(eq(APPLICATION_URL), eq(USERNAME), eq(PASSWORD), eq(EMAIL));
        assertSuccessResponse(response);
    }
}
