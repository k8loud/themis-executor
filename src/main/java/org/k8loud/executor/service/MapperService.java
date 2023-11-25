package org.k8loud.executor.service;

import org.k8loud.executor.model.ExecutionRQ;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MapperException;

public interface MapperService {
    @NotNull
    Action map(@NotNull ExecutionRQ executionRQ) throws MapperException, ActionException;
}
