package org.k8loud.executor.service;

import data.ExecutionRQ;
import org.k8loud.executor.action.Action;

public interface MapperService {

    Action map(ExecutionRQ executionRQ);
}
