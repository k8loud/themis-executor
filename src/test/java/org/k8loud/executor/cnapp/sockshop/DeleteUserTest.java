package org.k8loud.executor.cnapp.sockshop;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteUserTest extends SockShopBaseTest {
    @Override
    public void additionalSetUp() {
        when(sockShopPropertiesMock.getCustomersUrlSupplement()).thenReturn(SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT);
    }

    @Test
    void testDeleteUser() throws HTTPException, CNAppException, ValidationException, IOException {
        // given
        when(httpSessionMock.doDelete(anyString(), anyString())).thenReturn(successfulResponseMock);
        mockSuccessfulResponse();

        // when
        Map<String, Object> resultMap = sockShopService.deleteUser(APPLICATION_URL, ID);

        // then
        verify(httpSessionMock).doDelete(eq(APPLICATION_URL), eq(SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT + "/" + ID));
        assertResponseContent(resultMap);
    }

    @Test
    void testDeleteUserUnsuccessfulResponse() throws HTTPException {
        // given
        when(httpSessionMock.doDelete(anyString(), anyString())).thenReturn(unsuccessfulResponseMock);
        mockUnsuccessfulResponse();

        // when
        Throwable e = catchThrowable(() -> sockShopService.deleteUser(APPLICATION_URL, ID));

        // then
        assertThat(e).isExactlyInstanceOf(HTTPException.class).hasMessage(
                String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE));
        assertThat(((HTTPException) e).getExceptionCode()).isEqualTo(HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
    }
}
