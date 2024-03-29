package org.k8loud.executor.cnapp.sockshop;

import org.k8loud.executor.exception.CNAppException;
import org.k8loud.executor.exception.HTTPException;
import org.k8loud.executor.exception.MailException;
import org.k8loud.executor.exception.ValidationException;

import java.util.List;
import java.util.Map;

public interface SockShopService {
    Map<String, Object> registerUser(String applicationUrl, String username, String password, String email)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, Object> deleteUser(String applicationUrl, String userId)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, Object> createAddress(String applicationUrl, String username, String password, String userId,
                                      String country, String city, String postcode, String street, String number)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, Object> deleteAddress(String applicationUrl, String username, String password, String addressId)
            throws CNAppException, ValidationException, HTTPException;

    Map<String, Object> notifyCustomers(String applicationUrl, String senderDisplayName, String subject,
                                        String content, List<String> imagesUrls)
            throws CNAppException, HTTPException, MailException;
}
