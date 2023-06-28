package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.MapperException;

public interface MapperService {
    Action map(ExecutionRQ executionRQ) throws MapperException;
}
