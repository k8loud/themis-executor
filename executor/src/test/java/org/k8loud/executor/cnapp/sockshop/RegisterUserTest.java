package org.k8loud.executor.cnapp.sockshop;

import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

public class RegisterUserTest extends SockShopBaseTest {
    private static final String REGISTER_USER_URL_SUPPLEMENT = "customers";
    private static final String USERNAME = "userVal";
    private static final String PASSWORD = "passVal";
    private static final String EMAIL = "em@il.com";

    @Captor
    ArgumentCaptor<RegisterUserParams> registerUserParamsCaptor;

//    @Test
//    void testRegisterUser() throws CNAppException, HTTPException {
//        // given
//        when(sockShopProperties.getRegisterUserUrlSupplement()).thenReturn(REGISTER_USER_URL_SUPPLEMENT);
//        when(httpService.doPost(anyString(), anyString(), any())).thenReturn(SUCCESSFUL_RESPONSE);
//
//        // when
//        sockShopService.registerUser(APPLICATION_URL, USERNAME, PASSWORD, EMAIL);
//
//        // then
//        verify(httpService).doPost(eq(APPLICATION_URL), eq(REGISTER_USER_URL_SUPPLEMENT),
//                registerUserParamsCaptor.capture());
//        final RegisterUserParams registerUserParams = registerUserParamsCaptor.getValue();
//        assertEquals(USERNAME, registerUserParams.getUsername());
//        assertEquals(PASSWORD, registerUserParams.getPassword());
//        assertEquals(EMAIL, registerUserParams.getEmail());
//    }
//
//    @Test
//    void testRegisterUserUnsuccessfulResponse() throws HTTPException {
//        // given
//        when(sockShopProperties.getRegisterUserUrlSupplement()).thenReturn(REGISTER_USER_URL_SUPPLEMENT);
//        when(httpService.doPost(anyString(), anyString(), any())).thenReturn(UNSUCCESSFUL_RESPONSE);
//
//        // when
//        Throwable e = catchThrowable(() -> sockShopService.registerUser(APPLICATION_URL, USERNAME, PASSWORD, EMAIL));
//
//        // then
//        assertThat(e).isExactlyInstanceOf(CNAppException.class).hasMessage(
//                new HTTPException(String.valueOf(UNSUCCESSFUL_RESPONSE_STATUS_CODE),
//                        HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL).toString());
//        assertThat(((CNAppException) e).getExceptionCode()).isEqualTo(CAUGHT_HTTP_EXCEPTION);
//    }
}
