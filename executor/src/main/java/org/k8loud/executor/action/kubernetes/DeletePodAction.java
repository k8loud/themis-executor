package org.k8loud.executor.action.kubernetes;

import data.ExecutionRS;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;

import java.util.Map;

public class DeletePodAction extends Action {
    public DeletePodAction(Map<String, String> params) throws ActionException {
        super(params);
    }

    @Override
    public void unpackParams(Map<String, String> params) {
        // TODO: implement
    }

    @Override
    public ExecutionRS perform() {
        return null;
    }
}
