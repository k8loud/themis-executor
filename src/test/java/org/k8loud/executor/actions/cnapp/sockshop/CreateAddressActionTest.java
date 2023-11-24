package org.k8loud.executor.actions.cnapp.sockshop;

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

public class CreateAddressActionTest extends SockShopActionBaseTest {
    private static final String COUNTRY = "Java";
    private static final String CITY = "Cracow";
    private static final String POSTCODE = "31-005";
    private static final String STREET = "Bracka";
    private static final String NUMBER = "3";
    private static final Params PARAMS = new Params(Map.of(
            APPLICATION_URL_KEY, APPLICATION_URL,
            USERNAME_KEY, USERNAME,
            PASSWORD_KEY, PASSWORD,
            "userId", ID,
            "country", COUNTRY,
            "city", CITY,
            "postcode", POSTCODE,
            "street", STREET,
            "number", NUMBER
    ));

    @Test
    void testValidParams() throws CNAppException, ActionException, ValidationException, HTTPException {
        // given
        CreateAddressAction createAddressAction = new CreateAddressAction(PARAMS, sockShopServiceMock);
        when(sockShopServiceMock.createAddress(anyString(), anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString())).thenReturn(resultMap);

        // when
        ExecutionRS response = createAddressAction.execute();

        // then
        verify(sockShopServiceMock).createAddress(eq(APPLICATION_URL), eq(USERNAME), eq(PASSWORD), eq(ID), eq(COUNTRY),
                eq(CITY), eq(POSTCODE), eq(STREET), eq(NUMBER));
        assertSuccessResponse(response);
    }
}
