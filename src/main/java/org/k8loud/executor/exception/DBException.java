package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class DBException extends CustomException {

    public DBException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public DBException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public DBException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
