package org.k8loud.executor.service;

import data.ExecutionRS;
import org.k8loud.executor.action.Action;

public interface ExecutionService {
    ExecutionRS execute(Action action);
}
