package org.k8loud.executor.service;

import data.ExecutionRQ;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExecutionWrapperServiceImpl implements ExecutionWrapperService {
    private final ExecutionService executionService;
    private final MapperService mapperService;


    public ExecutionWrapperServiceImpl(ExecutionService executionService, MapperService mapperService) {
        this.executionService = executionService;
        this.mapperService = mapperService;
    }

    @Override
    public ResponseEntity<String> execute(ExecutionRQ request) {
        //TODO interceptor
        Action action = mapperService.map(request);
        if (action == null) {
            executionService.execute(action);
        } else {
            return new ResponseEntity<>("dupa", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Jest git", HttpStatus.OK);
    }
}
