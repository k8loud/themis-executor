package org.k8loud.executor.cnapp.sockshop;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.cnapp.sockshop.params.CreateAddressParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateAddressTest extends SockShopBaseTest {
    private static final String COUNTRY = "Zimbabwe";
    private static final String CITY = "Radom";
    private static final String POSTCODE = "23-123";
    private static final String STREET = "Wartka";
    private static final String NUMBER = "23";

    @Captor
    ArgumentCaptor<CreateAddressParams> createAddressParamsCaptor;

    @Override
    protected void additionalSetUp() throws HTTPException, IOException {
        when(sockShopPropertiesMock.getAddressesUrlSupplement()).thenReturn(SOCKSHOP_ADDRESSES_URL_SUPPLEMENT);
        mockAuth();
    }

    @Test
    void testCreateAddress() throws HTTPException, CNAppException, ValidationException {
        // given
        when(httpSessionMock.doPost(anyString(), anyString(), any())).thenReturn(successfulResponseMock);

        // when
        Map<String, Object> resultMap = sockShopService.createAddress(APPLICATION_URL, USERNAME, PASSWORD, ID, COUNTRY,
                CITY, POSTCODE, STREET, NUMBER);

        // then
        verify(httpSessionMock).doPost(eq(APPLICATION_URL), eq(SOCKSHOP_ADDRESSES_URL_SUPPLEMENT),
                createAddressParamsCaptor.capture());
        final CreateAddressParams createAddressParams = createAddressParamsCaptor.getValue();
        assertEquals(ID, createAddressParams.getId());
        assertEquals(COUNTRY, createAddressParams.getCountry());
        assertEquals(CITY, createAddressParams.getCity());
        assertEquals(POSTCODE, createAddressParams.getPostcode());
        assertEquals(STREET, createAddressParams.getStreet());
        assertEquals(NUMBER, createAddressParams.getNumber());
        assertResponseContent(resultMap);
    }

    @Test
    void testCreateAddressUnsuccessfulResponse() throws HTTPException {
        // given
        when(httpSessionMock.doPost(anyString(), anyString(), any())).thenReturn(unsuccessfulResponseMock);
        mockUnsuccessfulResponse();

        // when
        Throwable e = catchThrowable(() -> sockShopService.createAddress(APPLICATION_URL, USERNAME, PASSWORD, ID,
                COUNTRY, CITY, POSTCODE, STREET, NUMBER));

        // then
        assertThat(e).isExactlyInstanceOf(HTTPException.class).hasMessage(
                String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE));
        assertThat(((HTTPException) e).getExceptionCode()).isEqualTo(HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
    }
}
