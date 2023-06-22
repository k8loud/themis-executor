package org.k8loud.executor.action.kubernetes;

import data.ExecutionRS;
import org.k8loud.executor.action.Action;

import java.util.Map;

public class DeletePodAction extends Action {
    public DeletePodAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public ExecutionRS perform() {
        return null;
    }
}
