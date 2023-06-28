package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.springframework.http.ResponseEntity;

public interface ExecutionWrapperService {
    ResponseEntity<String> execute(ExecutionRQ request);
}
