package org.k8loud.executor.exception;

import org.jetbrains.annotations.NotNull;

public class KubernetesException extends CustomException {
    public KubernetesException(@NotNull Enum exceptionCode) {
        super(exceptionCode);
    }

    public KubernetesException(Exception e, @NotNull Enum exceptionCode) {
        super(e, exceptionCode);
    }

    public KubernetesException(String message, @NotNull Enum exceptionCode) {
        super(message, exceptionCode);
    }
}
