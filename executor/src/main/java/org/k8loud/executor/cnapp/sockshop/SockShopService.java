package org.k8loud.executor.cnapp.sockshop;

import org.k8loud.executor.exception.CNAppException;

import java.util.Map;

public interface SockShopService {
    Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException;

    Map<String, String> deleteUser(String applicationUrl, String id) throws CNAppException;
}
