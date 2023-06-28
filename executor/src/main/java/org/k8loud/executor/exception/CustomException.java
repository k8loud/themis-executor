package org.k8loud.executor.exception;

import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@ToString
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
}
