package org.k8loud.executor.service;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MapperException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.model.ExecutionRQ;
import org.k8loud.executor.model.ExecutionRS;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static org.k8loud.executor.model.ExecutionExitCode.NOT_OK;

@Slf4j
@Service
public class ExecutionWrapperServiceImpl implements ExecutionWrapperService {
    private final ValidationService validationService;
    private final ExecutionService executionService;
    private final MapperService mapperService;

    public ExecutionWrapperServiceImpl(ValidationService validationService, ExecutionService executionService,
                                       MapperService mapperService) {
        this.validationService = validationService;
        this.executionService = executionService;
        this.mapperService = mapperService;
    }

    @Override
    public ResponseEntity<ExecutionRS> execute(@NotNull ExecutionRQ request) {
        try {
            validationService.validate(request);
            Action action = mapperService.map(request);
            ExecutionRS response = executionService.execute(action);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ValidationException e) {
            return logErrorAndRespond(e, HttpStatus.BAD_REQUEST);
        } catch (MapperException | ActionException e) {
            return logErrorAndRespond(e, HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private ResponseEntity<ExecutionRS> logErrorAndRespond(Exception e, HttpStatus httpStatus) {
        log.error(e.toString());
        ExecutionRS response = ExecutionRS.builder()
                .result(e.toString())
                .exitCode(NOT_OK)
                .build();
        return new ResponseEntity<>(response, httpStatus);
    }
}
