package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class MailException extends CustomException {
    public MailException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public MailException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public MailException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
