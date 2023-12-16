package org.k8loud.executor.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.util.ClassHelper;

import static org.k8loud.executor.exception.code.ActionExceptionCode.ACTION_CLASS_NOT_FOUND;

@Slf4j
public class ActionHelper {
    @NotNull
    public Class<?> getActionClass(String collectionName, String actionName) throws ActionException {
        String fullActionName = String.format("%s.%s", collectionName, actionName);
        String fullClassName = String.format("%s.%s", this.getClass().getPackageName(), fullActionName);
        try {
            return ClassHelper.getClassFromName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new ActionException(e, ACTION_CLASS_NOT_FOUND);
        }
    }
}
