package org.k8loud.executor.util;

public class Util {
    public static boolean notEmptyBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
