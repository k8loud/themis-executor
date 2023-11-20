package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class HTTPException extends CustomException {
    public HTTPException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public HTTPException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public HTTPException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
