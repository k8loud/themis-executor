package org.k8loud.executor.service;

import data.ActionRequest;
import org.k8loud.executor.action.Action;

public interface MapperService {

    Action map(ActionRequest actionRequest);
}
