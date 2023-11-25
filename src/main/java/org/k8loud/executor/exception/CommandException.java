package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class CommandException extends CustomException {
    public CommandException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public CommandException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public CommandException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
