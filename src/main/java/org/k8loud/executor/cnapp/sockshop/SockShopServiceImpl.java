package org.k8loud.executor.cnapp.sockshop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.jetbrains.annotations.Nullable;
import org.k8loud.executor.cnapp.sockshop.params.CreateAddressParams;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.datastorage.DataStorageService;
import org.k8loud.executor.exception.*;
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
import static org.k8loud.executor.exception.code.CNAppExceptionCode.SOCK_SHOP_NOTIFY_CUSTOMERS_FAILED;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL;
import static org.k8loud.executor.util.Util.getAllRegexMatches;
import static org.k8loud.executor.util.Util.resultMap;

@Slf4j
@Service
@AllArgsConstructor
public class SockShopServiceImpl implements SockShopService {
    private final SockShopProperties sockShopProperties;
    private final HTTPService httpService;
    private final MailService mailService;
    private final DataStorageService dataStorageService;

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "CNAppException",
            exceptionCode = "SOCK_SHOP_REGISTER_USER_FAILED")
    public Map<String, Object> registerUser(String applicationUrl, String username, String password, String email)
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
    public Map<String, Object> deleteUser(String applicationUrl, String userId) throws CNAppException,
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
    public Map<String, Object> createAddress(String applicationUrl, String username, String password, String userId,
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
    public Map<String, Object> deleteAddress(String applicationUrl, String username, String password, String addressId)
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
    public Map<String, Object> notifyCustomers(String applicationUrl, String senderDisplayName, String subject,
                                               String content, List<String> imagesUrls)
            throws CNAppException, HTTPException {
        log.info("Notifying customers; senderDisplayName = '{}', subject = '{}', content = '{}', imageUrls = {}",
                senderDisplayName, subject, content, imagesUrls);

        HttpResponse response = httpService.createSession().doGet(applicationUrl,
                sockShopProperties.getCustomersUrlSupplement());
        String responseContent = handleResponse(response);

        final String MAIL_PATTERN = "\"username\":\"([a-zA-Z0-9]+@[a-zA-Z0-9.]+)\"";
        List<String> receivers = getAllRegexMatches(MAIL_PATTERN, responseContent, 1);
        Map<String, @Nullable CustomException> results = new HashMap<>();
        receivers.parallelStream().forEach(receiver -> {
            try {
                if (imagesUrls.isEmpty()) {
                    mailService.sendMail(receiver, senderDisplayName, subject, content)
                            .join();
                } else {
                    Map<String, String> imageTitleToPath = new HashMap<>();
                    for (String imageUrl : imagesUrls) {
                        // FIXME: Generalize,
                        //  parsing expects URLs like http://localhost:8082/catalogue/images/youtube_2.jpeg
                        String imageTitle = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                        imageTitle = imageTitle.substring(0, imageTitle.indexOf("."));
                        String imagePath = dataStorageService.storeImage(imageTitle, imageUrl);
                        imageTitleToPath.put(imageTitle, imagePath);
                    }
                    mailService.sendMailWithEmbeddedImages(receiver, senderDisplayName, subject, content,
                                    imageTitleToPath)
                            .join();
                    imageTitleToPath.forEach((key, value) -> dataStorageService.remove(value));
                }
                results.put(receiver, null);
            } catch (MailException | DataStorageException e) {
                results.put(receiver, e);
            }
        });

        if (results.entrySet().stream().anyMatch(e -> e.getValue() != null)) {
            throw new CNAppException(String.format("Failed to notify customers; senderDisplayName = '%s'; " +
                            "subject = '%s', content = '%s', imageUrls = {}. At least one mail failed to be sent. " +
                            "List of results: %s", senderDisplayName, subject, content, results),
                    SOCK_SHOP_NOTIFY_CUSTOMERS_FAILED);
        }

        return resultMap(String.format("Notified customers; senderDisplayName = '%s', subject = '%s', " +
                        "content = '%s', imageUrls = %s", senderDisplayName, subject, content, imagesUrls));
    }

    private String handleResponse(HttpResponse response) throws HTTPException, CNAppException {
        final int statusCode = response.getStatusLine().getStatusCode();
        if (!httpService.isStatusCodeSuccessful(statusCode)) {
            throw new HTTPException(String.valueOf(statusCode), HTTP_RESPONSE_STATUS_CODE_NOT_SUCCESSFUL);
        }
        try {
            return httpService.getResponseEntityAsString(response);
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

    private Map<String, Object> createResultMap(String result, String responseContent) throws ValidationException {
        return resultMap(result, Map.of("responseContent", responseContent));
    }
}
