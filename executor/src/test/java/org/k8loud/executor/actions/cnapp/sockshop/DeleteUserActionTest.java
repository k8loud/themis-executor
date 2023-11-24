package org.k8loud.executor.actions.cnapp.sockshop;

import data.ExecutionRS;
import data.Params;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteUserActionTest extends SockShopActionBaseTest {
    private static final Params PARAMS = new Params(Map.of(
            APPLICATION_URL_KEY, APPLICATION_URL,
            "userId", ID
    ));

    @Test
    void testValidParams() throws CNAppException, ActionException, ValidationException, HTTPException {
        // given
        DeleteUserAction deleteUserAction = new DeleteUserAction(PARAMS, sockShopServiceMock);
        when(sockShopServiceMock.deleteUser(anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = deleteUserAction.execute();

        // then
        verify(sockShopServiceMock).deleteUser(eq(APPLICATION_URL), eq(ID));
        assertSuccessResponse(response);
    }
}
