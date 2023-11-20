package org.k8loud.executor.cnapp.sockshop;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.k8loud.executor.cnapp.sockshop.params.RegisterUserParams;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.service.HTTPService;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.k8loud.executor.exception.code.CNAppExceptionCode.CAUGHT_HTTP_EXCEPTION;
import static org.k8loud.executor.exception.code.HTTPExceptionCode.HTTP_REQUEST_STATUS_CODE_NOT_SUCCESSFUL;
import static org.k8loud.executor.util.Util.resultMap;

@Slf4j
@Service
@AllArgsConstructor
public class SockShopServiceImpl implements SockShopService {
    private final SockShopProperties sockShopProperties;
    private final HTTPService httpService;

    @Override
    public Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException {
        log.info("Registering user {} with email {}", username, email);
        try {
            HttpResponse response = httpService.doPost(applicationUrl, sockShopProperties.getRegisterUserUrlSupplement(),
                    RegisterUserParams.builder().username(username).password(password).email(email).build());
            final int statusCode = response.getStatusLine().getStatusCode();
            if (!httpService.isResponseStatusCodeSuccessful(statusCode)) {
                throw new HTTPException(String.valueOf(statusCode), HTTP_REQUEST_STATUS_CODE_NOT_SUCCESSFUL);
            }
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
        return resultMap(String.format("Registered user %s with email %s", username, email));
    }

    @Override
    public Map<String, String> deleteUser(String applicationUrl, String id) throws CNAppException {
        log.info("Deleting user with id {}", id);
        try {
            HttpResponse response = httpService.doDelete(applicationUrl,
                    sockShopProperties.getCustomersUrlSupplement() + "/" + id);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (!httpService.isResponseStatusCodeSuccessful(statusCode)) {
                throw new HTTPException(String.valueOf(statusCode), HTTP_REQUEST_STATUS_CODE_NOT_SUCCESSFUL);
            }
        } catch (HTTPException e) {
            throw new CNAppException(e.toString(), CAUGHT_HTTP_EXCEPTION);
        }
        return resultMap(String.format("Deleted user with id %s", id));
    }
}
