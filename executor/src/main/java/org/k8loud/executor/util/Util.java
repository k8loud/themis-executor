package org.k8loud.executor.util;

public final class Util {
    private Util() {
        // Meant for static methods only, don't instantiate
    }

    public static boolean emptyOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String getFullResourceName(String resourceType, String resourceName) {
        return String.format("%s/%s", resourceType, resourceName);
    }
}
