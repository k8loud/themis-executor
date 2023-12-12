package org.k8loud.executor.controller;


import org.k8loud.executor.model.ExecutionRQ;
import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.service.ExecutionWrapperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutionController {
    private final ExecutionWrapperService executionWrapperService;

    public ExecutionController(ExecutionWrapperService executionWrapperService) {
        this.executionWrapperService = executionWrapperService;
    }

    @PostMapping(value = "/execute", consumes = "application/json")
    public ResponseEntity<ExecutionRS> execute(@RequestBody ExecutionRQ executionRQ) {
        return executionWrapperService.execute(executionRQ);
    }
}
