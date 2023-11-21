package org.k8loud.executor.cnapp.sockshop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.k8loud.executor.cnapp.sockshop.params.CreateAddressParams;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.service.HTTPService;
import org.k8loud.executor.util.HTTPSession;
import org.k8loud.executor.util.Util;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.k8loud.executor.exception.code.CNAppExceptionCode.CAUGHT_HTTP_EXCEPTION;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.k8loud.executor.util.Util.resultMap;

@Slf4j
@Service
@AllArgsConstructor
public class SockShopServiceImpl implements SockShopService {
    private final SockShopProperties sockShopProperties;
    private final HTTPService httpService;

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_REGISTER_USER_FAILED")
    public Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException {
        log.info("Registering user {} with email {}", username, email);
        try {
            HttpResponse response = httpService.createSession().doPost(applicationUrl,
                    sockShopProperties.getRegisterUserUrlSupplement(),
                    RegisterUserParams.builder().username(username).password(password).email(email).build());
            handleResponse(response);
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
        return resultMap(String.format("Registered user %s with email %s", username, email));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_DELETE_USER_FAILED")
    public Map<String, String> deleteUser(String applicationUrl, String id) throws CNAppException {
        log.info("Deleting user with id {}", id);
        try {
            HttpResponse response = httpService.createSession().doDelete(applicationUrl,
                    String.format("%s/%s", sockShopProperties.getCustomersUrlSupplement(), id));
            handleResponse(response);
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
        return resultMap(String.format("Deleted user with id %s", id));
    }


//    function login() {
//        var username = $('#username-modal').val();
//        var password = $('#password-modal').val();
//        $.ajax({
//                url: "login",
//                type: "GET",
//                async: false,
//                success: function (data, textStatus, jqXHR) {
//            $("#login-message").html('<div class="alert alert-success">Login successful.</div>');
//            console.log('posted: ' + textStatus);
//            console.log("logged_in cookie: " + $.cookie('logged_in'));
//            setTimeout(function(){
//                location.reload();
//            }, 1500);
//        },
//        error: function (jqXHR, textStatus, errorThrown) {
//            $("#login-message").html('<div class="alert alert-danger">Invalid login credentials.</div>');
//            console.log('error: ' + JSON.stringify(jqXHR));
//            console.log('error: ' + textStatus);
//            console.log('error: ' + errorThrown);
//        },
//        beforeSend: function (xhr) {
//            xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password));
//        }
//    });
//        return false;
//    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException", exceptionCode = "SOCK_SHOP_CREATE_ADDRESS")
    public Map<String, String> createAddress(String applicationUrl, String username, String password, String id,
                                             String country, String city, String postcode, String street, String number)
            throws CNAppException {
        CreateAddressParams createAddressParams = CreateAddressParams.builder().id(id).country(country)
                .city(city).street(street).number(number).postcode(postcode).build();
        log.info("Creating address with params {}", createAddressParams);
        try {
            HTTPSession session = httpService.createSession();

            // url: "login", type: "GET"
            // xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password))
            HttpResponse response = session.doGet(applicationUrl, sockShopProperties.getLoginUserUrlSupplement(),
                    addAuthHeader(new HashMap<>(), username, password));
            handleResponse(response);

            response = session.doPost(applicationUrl, sockShopProperties.getAddressesUrlSupplement(),
                    createAddressParams);
            handleResponse(response);
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
        return resultMap(String.format("Created address with params %s", createAddressParams));
    }

    private void handleResponse(HttpResponse response) throws HTTPException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (!httpService.isStatusCodeSuccessful(statusCode)) {
            throw new HTTPException(String.valueOf(statusCode), HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
        }
    }

    private Map<String, String> addAuthHeader(Map<String, String> headers, String username, String password) {
        final String authPhrase = username + ":" + password;
        headers.put("Authorization", "Basic " + Util.encodeBase64(authPhrase));
        return headers;
    }
}
