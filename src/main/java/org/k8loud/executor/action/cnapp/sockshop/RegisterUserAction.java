package org.k8loud.executor.action.cnapp.sockshop;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

public class RegisterUserAction extends SockShopAction {
    private String username;
    private String password;
    private String email;

    public RegisterUserAction(Params params, SockShopService sockShopService) throws ActionException {
        super(params, sockShopService);
    }

    @Builder
    public RegisterUserAction(SockShopService sockShopService, String applicationUrl,
                              String username, String password, String email) {
        super(sockShopService, applicationUrl);
        this.username = username;
        this.password = password;
        this.email = email;
    }

    @Override
    public void unpackAdditionalParams(Params params) {
        username = params.getRequiredParam("username");
        password = params.getRequiredParam("password");
        email = params.getRequiredParam("email");
    }

    @Override
    protected Map<String, String> executeBody() throws CNAppException, ValidationException, HTTPException {
        return sockShopService.registerUser(applicationUrl, username, password, email);
    }
}
