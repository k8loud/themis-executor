package org.k8loud.executor.cnapp.sockshop;

import org.k8loud.executor.exception.CNAppException;

import java.util.Map;

public interface SockShopService {
    Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException;

    Map<String, String> deleteUser(String applicationUrl, String id) throws CNAppException;

    Map<String, String> createAddress(String applicationUrl, String username, String password, String id,
                                      String country, String city, String postcode, String street, String number)
            throws CNAppException;

    Map<String, String> deleteAddress(String applicationUrl, String username, String password, String id)
            throws CNAppException;
}
