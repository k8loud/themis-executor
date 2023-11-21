package org.k8loud.executor.util;

import org.k8loud.executor.exception.ValidationException;

import java.util.*;

import static org.k8loud.executor.exception.code.ValidationExceptionCode.ADDITIONAL_DATA_WRONG_KEY;

public final class Util {
    private Util() {
        // Meant for static methods only, don't instantiate
    }

    public static boolean emptyOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String nameWithUuid(String s) {
        return String.format("%s-%s", s, UUID.randomUUID().toString().substring(0, 8));
    }

    public static String getFullResourceName(String resourceType, String resourceName, String delimiter) {
        return String.format("%s%s%s", resourceType, delimiter, resourceName);
    }

    public static String getFullResourceName(String resourceType, String resourceName) {
        return getFullResourceName(resourceType, resourceName, "/");
    }

    public static Map<String, String> resultMap(String result){
        return new HashMap<>(Map.of("result", result));
    }

    @SuppressWarnings("unchecked")
    public static Map<String, String> resultMap(String result, Map<String, String> additionalData)
            throws ValidationException {
        if (additionalData.containsKey("result")) {
            throw new ValidationException(ADDITIONAL_DATA_WRONG_KEY);
        }
        Set<Map.Entry<String, String>> entries = additionalData.entrySet();
        Map<String, String> resultMap = new HashMap<>(Map.ofEntries(entries.toArray(Map.Entry[]::new)));
        resultMap.put("result", result);
        return resultMap;
    }

    public static String encodeBase64(String toEncode) {
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}
