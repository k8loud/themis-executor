package org.k8loud.executor.service;

import data.ExecutionRQ;
import data.ExecutionRS;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutionWrapperServiceImpl implements ExecutionWrapperService {
    private final ValidationService validationService;
    private final ExecutionService executionService;
    private final MapperService mapperService;

    public ExecutionWrapperServiceImpl(ValidationService validationService, ExecutionService executionService, MapperService mapperService) {
        this.validationService = validationService;
        this.executionService = executionService;
        this.mapperService = mapperService;
    }

    @Override
    public ResponseEntity<String> execute(ExecutionRQ request) {
        try {
            validationService.validate(request);
            Action action = mapperService.map(request);
            ExecutionRS response = executionService.execute(action);
            return new ResponseEntity<>(response.toString(), HttpStatus.OK);
        } catch (ValidationException e) {
            return logErrorAndRespond(e, HttpStatus.BAD_REQUEST);
        } catch (MapperException e) {
            return logErrorAndRespond(e, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private ResponseEntity<String> logErrorAndRespond(Exception e, HttpStatus httpStatus) {
        log.error(e.toString());
        return new ResponseEntity<>(e.toString(), httpStatus);
    }
}
