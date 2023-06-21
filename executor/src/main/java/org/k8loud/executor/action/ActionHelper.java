package org.k8loud.executor.action;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActionHelper {
    @Nullable
    public Class<?> getActionClass(String collectionName, String actionName) {
        String fullActionName = String.format("%s.%s", collectionName, actionName);
        Class<?> clazz = null;
        try {
            String packageName = this.getClass().getPackageName();
            String fullClassName = String.format("%s.%s", packageName, fullActionName);
            clazz = Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            log.error("Couldn't find class for action `{}`", fullActionName);
        }
        return clazz;
    }
}
