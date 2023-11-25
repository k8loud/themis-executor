package org.k8loud.executor.action.cnapp.sockshop;

import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;
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

public class DeleteAddressActionTest extends SockShopActionBaseTest {
    private static final Params PARAMS = new Params(Map.of(
            APPLICATION_URL_KEY, APPLICATION_URL,
            USERNAME_KEY, USERNAME,
            PASSWORD_KEY, PASSWORD,
            "addressId", ID
    ));

    @Test
    void testValidParams() throws CNAppException, ActionException, ValidationException, HTTPException {
        // given
        DeleteAddressAction deleteAddressAction = new DeleteAddressAction(PARAMS, sockShopServiceMock);
        when(sockShopServiceMock.deleteAddress(anyString(), anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = deleteAddressAction.execute();

        // then
        verify(sockShopServiceMock).deleteAddress(eq(APPLICATION_URL), eq(USERNAME), eq(PASSWORD), eq(ID));
        assertSuccessResponse(response);
    }
}
