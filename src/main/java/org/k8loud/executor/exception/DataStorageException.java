package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class DataStorageException extends CustomException {
    public DataStorageException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public DataStorageException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public DataStorageException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
