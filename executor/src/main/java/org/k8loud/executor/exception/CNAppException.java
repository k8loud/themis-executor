package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class CNAppException extends CustomException {
    public CNAppException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public CNAppException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public CNAppException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
