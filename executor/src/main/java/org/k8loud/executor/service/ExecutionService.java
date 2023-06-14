package org.k8loud.executor.service;

import org.k8loud.executor.action.Action;

public interface ExecutionService {

    void execute(Action action);
}
