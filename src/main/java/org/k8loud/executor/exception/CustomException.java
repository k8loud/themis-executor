package org.k8loud.executor.exception;

import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

@Getter
public class CustomException extends Exception {
    @NonNull
    protected final Enum exceptionCode;

    public CustomException(@NotNull Enum exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public CustomException(Exception e, @NotNull Enum exceptionCode) {
        super(e.getMessage(), e.getCause());
        this.exceptionCode = exceptionCode;
    }

    public CustomException(String message, @NotNull Enum exceptionCode) {
        super(message);
        this.exceptionCode = exceptionCode;
    }

    @Override
    public String toString() {
        String classAndExceptionCode = String.format("%s [%s]", getClass().getName(), exceptionCode);
        String message = getLocalizedMessage();
        return (message != null) ? (classAndExceptionCode + ": " + message) : classAndExceptionCode;
    }
}
