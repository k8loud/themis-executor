package org.k8loud.executor.cnapp.sockshop;

import org.junit.jupiter.api.Test;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RegisterUserTest extends SockShopBaseTest {
    private static final String EMAIL = "em@il.com";

    @Captor
    ArgumentCaptor<RegisterUserParams> registerUserParamsCaptor;

    @Override
    protected void additionalSetUp() {
        when(sockShopPropertiesMock.getRegisterUserUrlSupplement()).thenReturn(REGISTER_USER_URL_SUPPLEMENT);
    }

    @Test
    void testRegisterUser() throws CNAppException, HTTPException, ValidationException {
        // given
        when(httpSessionMock.doPost(anyString(), anyString(), any())).thenReturn(successfulResponseMock);
        mockSuccessfulResponse();

        // when
        Map<String, String> resultMap = sockShopService.registerUser(APPLICATION_URL, USERNAME, PASSWORD, EMAIL);

        // then
        verify(httpSessionMock).doPost(eq(APPLICATION_URL), eq(REGISTER_USER_URL_SUPPLEMENT),
                registerUserParamsCaptor.capture());
        final RegisterUserParams registerUserParams = registerUserParamsCaptor.getValue();
        assertEquals(USERNAME, registerUserParams.getUsername());
        assertEquals(PASSWORD, registerUserParams.getPassword());
        assertEquals(EMAIL, registerUserParams.getEmail());
        assertResponseContent(resultMap);
    }

    @Test
    void testRegisterUserUnsuccessfulResponse() throws HTTPException {
        // given
        when(httpSessionMock.doPost(anyString(), anyString(), any())).thenReturn(unsuccessfulResponseMock);
        mockUnsuccessfulResponse();

        // when
        Throwable e = catchThrowable(() -> sockShopService.registerUser(APPLICATION_URL, USERNAME, PASSWORD, EMAIL));

        // then
        assertThat(e).isExactlyInstanceOf(HTTPException.class).hasMessage(
                String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE));
        assertThat(((HTTPException) e).getExceptionCode()).isEqualTo(HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
    }
}
