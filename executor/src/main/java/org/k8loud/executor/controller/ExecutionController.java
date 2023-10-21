package org.k8loud.executor.controller;


import data.ExecutionRQ;
import org.k8loud.executor.drools.DroolsService;
import org.k8loud.executor.dto.OrderDiscount;
import org.k8loud.executor.dto.OrderRequest;
import org.k8loud.executor.service.ExecutionWrapperService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecutionController {

    private final DroolsService droolsService;
    private final ExecutionWrapperService executionWrapperService;

    public ExecutionController(DroolsService droolsService, ExecutionWrapperService executionWrapperService) {
        this.droolsService = droolsService;
        this.executionWrapperService = executionWrapperService;
    }

    @PostMapping(value = "/execute", consumes = "application/json")
    public ResponseEntity<String> execute(@RequestBody ExecutionRQ executionRQ) {
        return executionWrapperService.execute(executionRQ);
    }

    @PostMapping(value = "/drool", consumes = "application/json")
    public ResponseEntity<OrderDiscount> drools(@RequestBody OrderRequest rq) {
        return new ResponseEntity<>(droolsService.getDiscount(rq), HttpStatus.OK);
    }


}
