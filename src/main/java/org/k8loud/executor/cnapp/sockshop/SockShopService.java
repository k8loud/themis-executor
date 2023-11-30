package org.k8loud.executor.cnapp.sockshop;

import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

public interface SockShopService {
    Map<String, String> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, String> deleteUser(String applicationUrl, String userId)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, String> createAddress(String applicationUrl, String username, String password, String userId,
                                      String country, String city, String postcode, String street, String number)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, String> deleteAddress(String applicationUrl, String username, String password, String addressId)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, String> notifyCustomers(String applicationUrl, String message)
            throws CNAppException, ValidationException, HTTPException, MailException;
}
