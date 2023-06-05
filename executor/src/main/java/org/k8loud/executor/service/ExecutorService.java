package org.k8loud.executor.service;

import org.k8loud.executor.action.Action;

public interface ExecutorService {

    void execute(Action action);
}
