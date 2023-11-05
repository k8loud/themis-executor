package org.k8loud.executor.actions.kubernetes;

import data.ExecutionRS;
import data.Params;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;


public class DeletePodAction extends Action {
    public DeletePodAction(Params params) throws ActionException {
        super(params);
    }

    @Override
    public void unpackParams(Params params) {
        // TODO: implement
    }

    @Override
    public ExecutionRS perform() {
        return null;
    }
}
