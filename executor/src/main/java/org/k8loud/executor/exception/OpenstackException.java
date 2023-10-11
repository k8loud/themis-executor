package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class OpenstackException extends CustomException {
    public OpenstackException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public OpenstackException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public OpenstackException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
