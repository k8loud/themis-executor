package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRQ;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

public interface ExecutionWrapperService {
    ResponseEntity<String> execute(@NotNull ExecutionRQ request);
}
