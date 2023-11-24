package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRS;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.actions.Action;

public interface ExecutionService {
    @NotNull
    ExecutionRS execute(@NotNull Action action);
}
