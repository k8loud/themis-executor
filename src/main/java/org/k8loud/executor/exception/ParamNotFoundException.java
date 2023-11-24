package org.k8loud.executor.exception;

public class ParamNotFoundException extends RuntimeException {
    public ParamNotFoundException() {
    }

    public ParamNotFoundException(String message) {
        super(message);
    }
}
