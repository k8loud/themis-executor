package org.k8loud.executor.service;


import data.ExecutionRQ;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.ValidationException;

public interface ValidationService {
    void validate(@NotNull ExecutionRQ request) throws ValidationException;
}
