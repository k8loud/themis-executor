package org.k8loud.executor.action.kubernetes;

import org.k8loud.executor.action.Action;

import java.util.Map;

public class DeletePodAction extends Action {
    public DeletePodAction(Map<String, String> params) {
        super(params);
    }

    @Override
    public void perform() {

    }
}
