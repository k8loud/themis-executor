package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class ValidationException extends CustomException {
    public ValidationException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }
}
