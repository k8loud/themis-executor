package org.k8loud.executor.action;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.MapperException;

import static org.k8loud.executor.exception.code.MapperExceptionCode.ACTION_CLASS_NOT_FOUND;

@Slf4j
public class ActionHelper {
    @NotNull
    public Class<?> getActionClass(String collectionName, String actionName) throws MapperException {
        String fullActionName = String.format("%s.%s", collectionName, actionName);
        try {
            String packageName = this.getClass().getPackageName();
            String fullClassName = String.format("%s.%s", packageName, fullActionName);
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new MapperException(e, ACTION_CLASS_NOT_FOUND);
        }
    }
}
