package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.MapperException;

public interface MapperService {
    @NotNull
    Action map(@NotNull ExecutionRQ executionRQ) throws MapperException, ActionException;
}
