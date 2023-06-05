package org.k8loud.executor.service;

import data.ActionRequest;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutionServiceImpl implements ExecutionService {


    private final ValidationService validationService;
    private final ExecutorService executorService;
    private final MapperService mapperService;


    public ExecutionServiceImpl(ValidationService validationService, ExecutorService executorService, MapperService mapperService) {
        this.validationService = validationService;
        this.executorService = executorService;
        this.mapperService = mapperService;
    }

    @Override
    public ResponseEntity<String> execute(ActionRequest request) {
        //TODO interceptor
        if (validationService.validate(request)) {
            Action action = mapperService.map(request);
            executorService.execute(action);
        } else {
            return new ResponseEntity<>("dupa", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Jest git", HttpStatus.OK);
    }
}
