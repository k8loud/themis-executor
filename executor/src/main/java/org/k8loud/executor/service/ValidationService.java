package org.k8loud.executor.service;


import data.ExecutionRQ;
import org.k8loud.executor.exception.ValidationException;

public interface ValidationService {
    void validate(ExecutionRQ request) throws ValidationException;
}
