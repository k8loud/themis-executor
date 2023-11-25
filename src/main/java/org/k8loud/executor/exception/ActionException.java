package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class ActionException extends CustomException {
    public ActionException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public ActionException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }
}
