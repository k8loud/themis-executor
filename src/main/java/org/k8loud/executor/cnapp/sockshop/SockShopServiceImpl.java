package org.k8loud.executor.cnapp.sockshop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.Nullable;
import org.k8loud.executor.cnapp.sockshop.params.CreateAddressParams;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.http.HTTPService;
import org.k8loud.executor.http.HTTPSession;
import org.k8loud.executor.mail.MailService;
import org.k8loud.executor.util.Util;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.k8loud.executor.exception.code.CNAppExceptionCode.FAILED_TO_CONVERT_RESPONSE_ENTITY;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.k8loud.executor.exception.code.MailExceptionCode.FAILED_TO_SEND_MAIL;
import static org.k8loud.executor.util.Util.getAllRegexMatches;
import static org.k8loud.executor.util.Util.resultMap;

@Slf4j
@Service
@AllArgsConstructor
public class SockShopServiceImpl implements SockShopService {
    private final SockShopProperties sockShopProperties;
    private final HTTPService httpService;
    private final MailService mailService;

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_REGISTER_USER_FAILED")
    public Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException, ValidationException, HTTPException {
        log.info("Registering user {} with email {}", username, email);
        HttpResponse response = httpService.createSession().doPost(applicationUrl,
                sockShopProperties.getRegisterUserUrlSupplement(),
                RegisterUserParams.builder().username(username).password(password).email(email).build());
        String responseContent = handleResponse(response);
        return createResultMap(String.format("Registered user %s with email %s", username, email), responseContent);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_DELETE_USER_FAILED")
    public Map<String, String> deleteUser(String applicationUrl, String userId) throws CNAppException,
            ValidationException, HTTPException {
        log.info("Deleting user with id {}", userId);
        HttpResponse response = httpService.createSession().doDelete(applicationUrl,
                String.format("%s/%s", sockShopProperties.getCustomersUrlSupplement(), userId));
        String responseContent = handleResponse(response);
        return createResultMap(String.format("Deleted user with id %s", userId), responseContent);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_CREATE_ADDRESS_FAILED")
    public Map<String, String> createAddress(String applicationUrl, String username, String password, String userId,
                                             String country, String city, String postcode, String street, String number)
            throws CNAppException, ValidationException, HTTPException {
        CreateAddressParams createAddressParams = CreateAddressParams.builder().id(userId).country(country)
                .city(city).street(street).number(number).postcode(postcode).build();
        log.info("Creating address with params {}", createAddressParams);
        HTTPSession session = httpService.createSession();
        authSession(session, applicationUrl, username, password);
        HttpResponse response = session.doPost(applicationUrl, sockShopProperties.getAddressesUrlSupplement(),
                createAddressParams);
        String responseContent = handleResponse(response);
        return createResultMap(String.format("Created address with params %s", createAddressParams),
                responseContent);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_DELETE_ADDRESS_FAILED")
    public Map<String, String> deleteAddress(String applicationUrl, String username, String password, String addressId)
            throws CNAppException, ValidationException, HTTPException {
        log.info("Deleting address with id {}", addressId);
        HTTPSession session = httpService.createSession();
        authSession(session, applicationUrl, username, password);
        HttpResponse response = session.doDelete(applicationUrl, String.format("%s/%s",
                sockShopProperties.getAddressesUrlSupplement(), addressId));
        String responseContent = handleResponse(response);
        return createResultMap(String.format("Deleted address with id %s", addressId),
                responseContent);
    }

    // Assuming that email == username, it's not possible to access user's email via REST API
    // Should be changed when SendQueryToDbAction is added
    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_NOTIFY_CUSTOMERS_FAILED")
    public Map<String, String> notifyCustomers(String applicationUrl, String senderDisplayName, String subject,
                                               String content)
            throws CNAppException, ValidationException, HTTPException, MailException {
        log.info("Notifying customers; senderDisplayName = '{}'; subject = '{}', content = '{}'", senderDisplayName,
                subject, content);

        HttpResponse response = httpService.createSession().doGet(applicationUrl,
                sockShopProperties.getCustomersUrlSupplement());
        String responseContent = handleResponse(response);

        final String MAIL_PATTERN = "\"username\":\"([a-zA-Z0-9]+@[a-zA-Z0-9.]+)\"";
        List<String> receivers = getAllRegexMatches(MAIL_PATTERN, responseContent, 1);
        Map<String, @Nullable MailException> results = new HashMap<>();
        receivers.forEach(receiver -> {
            try {
                mailService.sendMail(receiver, senderDisplayName, subject, content);
                results.put(receiver, null);
            } catch (MailException e) {
                results.put(receiver, e);
            }
        });

        if (results.entrySet().stream().anyMatch(e -> e.getValue() != null)) {
            throw new MailException(String.format("Failed to notify customers; senderDisplayName = '%s'; " +
                            "subject = '%s', content = '%s'. At least one mail failed to be sent. List of results: %s",
                    senderDisplayName, subject, content, results), FAILED_TO_SEND_MAIL);
        }

        return resultMap(String.format("Notified customers; senderDisplayName = '%s'; subject = '%s', " +
                        "content = '%s'", senderDisplayName, subject, content));
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

    private void authSession(HTTPSession session, String applicationUrl, String username, String password)
            throws HTTPException, CNAppException {
        // url: "login", type: "GET"
        // xhr.setRequestHeader("Authorization", "Basic " + btoa(username + ":" + password))
        HttpResponse response = session.doGet(applicationUrl, sockShopProperties.getLoginUserUrlSupplement(),
                addAuthHeader(new HashMap<>(), username, password));
        handleResponse(response);
    }

    private Map<String, String> addAuthHeader(Map<String, String> headers, String username, String password) {
        final String authPhrase = username + ":" + password;
        headers.put("Authorization", "Basic " + Util.encodeBase64(authPhrase));
        return headers;
    }

    private Map<String, String> createResultMap(String result, String responseContent) throws ValidationException {
        return resultMap(result, Map.of("responseContent", responseContent));
    }
}
