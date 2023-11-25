package org.k8loud.executor.service;


import org.k8loud.executor.model.ExecutionRQ;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.exception.code.ValidationExceptionCode;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ValidationServiceImpl implements ValidationService {
    @Override
    public void validate(@NotNull ExecutionRQ request) throws ValidationException {
        if (null == request.getCollectionName()) {
            throw new ValidationException(ValidationExceptionCode.MISSING_COLLECTION_NAME);
        } else if (null == request.getActionName()) {
            throw new ValidationException(ValidationExceptionCode.MISSING_ACTION_NAME);
        } else if (null == request.getParams()) {
            throw new ValidationException(ValidationExceptionCode.MISSING_PARAMS);
        }
        // If no exception has been thrown the request is valid
    }
}
