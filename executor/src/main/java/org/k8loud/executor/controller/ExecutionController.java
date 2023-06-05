package org.k8loud.executor.controller;


import data.ActionRequest;
import org.k8loud.executor.service.ExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutionController {


    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    @PostMapping("/roman")
    public ResponseEntity<String> action(@RequestBody ActionRequest actionRQ) {
        return executionService.execute(actionRQ);
    }
}
