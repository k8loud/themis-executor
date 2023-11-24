package org.k8loud.executor.cnapp.sockshop;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteAddressTest extends SockShopBaseTest {
    @Override
    protected void additionalSetUp() throws HTTPException {
        when(sockShopPropertiesMock.getAddressesUrlSupplement()).thenReturn(SOCKSHOP_ADDRESSES_URL_SUPPLEMENT);
        mockAuth();
    }

    @Test
    void testDeleteAddress() throws HTTPException, CNAppException, ValidationException {
        // given
        when(httpSessionMock.doDelete(anyString(), anyString())).thenReturn(successfulResponseMock);

        // when
        Map<String, String> resultMap = sockShopService.deleteAddress(APPLICATION_URL, USERNAME, PASSWORD, ID);

        // then
        verify(httpSessionMock).doDelete(eq(APPLICATION_URL), eq(SOCKSHOP_ADDRESSES_URL_SUPPLEMENT + "/" + ID));
        assertResponseContent(resultMap);
    }

    @Test
    void testDeleteAddressUnsuccessfulResponse() throws HTTPException {
        // given
        when(httpSessionMock.doDelete(anyString(), anyString())).thenReturn(unsuccessfulResponseMock);
        mockUnsuccessfulResponse();

        // when
        Throwable e = catchThrowable(() -> sockShopService.deleteAddress(APPLICATION_URL, USERNAME, PASSWORD, ID));

        // then
        assertThat(e).isExactlyInstanceOf(HTTPException.class).hasMessage(
                String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE));
        assertThat(((HTTPException) e).getExceptionCode()).isEqualTo(HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
    }
}
