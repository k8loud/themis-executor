package org.k8loud.executor.cnapp.sockshop;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.k8loud.executor.exception.code.CNAppExceptionCode.CAUGHT_HTTP_EXCEPTION;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteUserTest extends SockShopBaseTest {
    private static final String SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT = "customers";
    private static final String ID = "655b6dfacb8de600019db115";

    @Test
    void testDeleteUser() throws HTTPException, CNAppException {
        // given
        when(sockShopProperties.getCustomersUrlSupplement()).thenReturn(SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT);
        when(httpService.doDelete(anyString(), anyString())).thenReturn(SUCCESSFUL_RESPONSE);

        // when
        sockShopService.deleteUser(APPLICATION_URL, ID);

        // then
        verify(httpService).doDelete(eq(APPLICATION_URL), eq(SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT + "/" + ID));
    }

    @Test
    void testDeleteUserUnsuccessfulResponse() throws HTTPException {
        // given
        when(sockShopProperties.getCustomersUrlSupplement()).thenReturn(SOCKSHOP_CUSTOMERS_URL_SUPPLEMENT);
        when(httpService.doDelete(anyString(), anyString())).thenReturn(UNSUCCESSFUL_RESPONSE);

        // when
        Throwable e = catchThrowable(() -> sockShopService.deleteUser(APPLICATION_URL, ID));

        // then
        assertThat(e).isExactlyInstanceOf(CNAppException.class).hasMessage(
                new HTTPException(String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE),
                        HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL).toString());
        assertThat(((CNAppException) e).getExceptionCode()).isEqualTo(CAUGHT_HTTP_EXCEPTION);
    }
}
