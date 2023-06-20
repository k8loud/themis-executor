package org.k8loud.executor.controller;


import data.ExecutionRQ;
import org.k8loud.executor.service.ExecutionWrapperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutorController {
    private final ExecutionWrapperService executionWrapperService;

    public ExecutorController(ExecutionWrapperService executionWrapperService) {
        this.executionWrapperService = executionWrapperService;
    }

    @PostMapping(value = "/execute", consumes = "application/json")
    public ResponseEntity<String> action(@RequestBody ExecutionRQ actionRQ) {
        return executionWrapperService.execute(actionRQ);
    }
}
