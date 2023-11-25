package org.k8loud.executor.action.cnapp;

import org.k8loud.executor.model.Params;
import lombok.AllArgsConstructor;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

@AllArgsConstructor
public abstract class CNAppAction extends Action {
    // Aggregates CNApp actions

    protected CNAppAction(Params params) throws ActionException {
        super(params);
    }
}
