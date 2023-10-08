package org.k8loud.executor.util;

import lombok.Getter;

@Getter
public class ClassParameter {
    private final Class<?> clazz;
    private final Object instance;


    public ClassParameter(Class<?> clazz, Object instance) {
        if (!clazz.isInstance(instance)) {
            throw new RuntimeException(String.format(
                    "Failed to initialize ClassParameter instance. Instance %s " + "class" + " is not instance of %s",
                    instance, clazz));
        }
        this.clazz = clazz;
        this.instance = instance;
    }
}
