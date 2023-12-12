package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRQ;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.model.ExecutionRS;
import org.springframework.http.ResponseEntity;

public interface ExecutionWrapperService {
    ResponseEntity<ExecutionRS> execute(@NotNull ExecutionRQ request);
}
