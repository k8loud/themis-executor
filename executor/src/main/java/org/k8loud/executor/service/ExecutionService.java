package org.k8loud.executor.service;

import data.ExecutionRS;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.action.Action;

public interface ExecutionService {
    @NotNull
    ExecutionRS execute(@NotNull Action action);
}
