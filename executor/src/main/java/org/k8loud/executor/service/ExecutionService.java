package org.k8loud.executor.service;

import data.ActionRequest;
import org.springframework.http.ResponseEntity;

public interface ExecutionService {

    ResponseEntity<String> execute(ActionRequest request);
}
