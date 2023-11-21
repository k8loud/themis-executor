package org.k8loud.executor.cnapp.sockshop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.k8loud.executor.cnapp.sockshop.params.CreateAddressParams;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.service.HTTPService;
import org.k8loud.executor.util.HTTPSession;
import org.k8loud.executor.util.Util;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.k8loud.executor.exception.code.CNAppExceptionCode.CAUGHT_HTTP_EXCEPTION;
import static org.k8loud.executor.exception.code.CNAppExceptionCode.FAILED_TO_CONVERT_RESPONSE_ENTITY;
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
            String responseContent = handleResponse(response);
            return resultMap(appendResponseContent(String.format("Registered user %s with email %s", username, email),
                    responseContent));
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_DELETE_USER_FAILED")
    public Map<String, String> deleteUser(String applicationUrl, String userId) throws CNAppException {
        log.info("Deleting user with id {}", userId);
        try {
            HttpResponse response = httpService.createSession().doDelete(applicationUrl,
                    String.format("%s/%s", sockShopProperties.getCustomersUrlSupplement(), userId));
            String responseContent = handleResponse(response);
            return resultMap(appendResponseContent(String.format("Deleted user with id %s", userId), responseContent));
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_CREATE_ADDRESS_FAILED")
    public Map<String, String> createAddress(String applicationUrl, String username, String password, String userId,
                                             String country, String city, String postcode, String street, String number)
            throws CNAppException {
        CreateAddressParams createAddressParams = CreateAddressParams.builder().id(userId).country(country)
                .city(city).street(street).number(number).postcode(postcode).build();
        log.info("Creating address with params {}", createAddressParams);
        try {
            HTTPSession session = authSession(httpService.createSession(), applicationUrl, username, password);
            HttpResponse response = session.doPost(applicationUrl, sockShopProperties.getAddressesUrlSupplement(),
                    createAddressParams);
            String responseContent = handleResponse(response);
            return resultMap(appendResponseContent(String.format("Created address with params %s", createAddressParams),
                    responseContent));
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_DELETE_ADDRESS_FAILED")
    public Map<String, String> deleteAddress(String applicationUrl, String username, String password, String addressId)
            throws CNAppException {
        log.info("Deleting address with id {}", addressId);
        try {
            HTTPSession session = authSession(httpService.createSession(), applicationUrl, username, password);
            HttpResponse response = session.doDelete(applicationUrl, String.format("%s/%s",
                    sockShopProperties.getAddressesUrlSupplement(), addressId));
            String responseContent = handleResponse(response);
            return resultMap(appendResponseContent(String.format("Deleted address with id %s", addressId),
                    responseContent));
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
    }

    private String handleResponse(HttpResponse response) throws HTTPException, CNAppException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (!httpService.isStatusCodeSuccessful(statusCode)) {
            throw new HTTPException(String.valueOf(statusCode), HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
        }
        try {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            throw new CNAppException(e.toString(), FAILED_TO_CONVERT_RESPONSE_ENTITY);
        }
    }

    private HTTPSession authSession(HTTPSession session, String applicationUrl, String username, String password)
            throws HTTPException, CNAppException {
        // url: "login", type: "GET"
        // xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password))
        HttpResponse response = session.doGet(applicationUrl, sockShopProperties.getLoginUserUrlSupplement(),
                addAuthHeader(new HashMap<>(), username, password));
        handleResponse(response);
        return session;
    }

    private Map<String, String> addAuthHeader(Map<String, String> headers, String username, String password) {
        final String authPhrase = username + ":" + password;
        headers.put("Authorization", "Basic " + Util.encodeBase64(authPhrase));
        return headers;
    }

    private String appendResponseContent(String result, String responseContent) {
        return String.format("%s; response content: '%s'", result, responseContent);
    }
}
