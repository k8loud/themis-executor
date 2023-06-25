package org.k8loud.executor.action;


import data.ExecutionRS;
import lombok.Data;

import java.util.Map;

@Data
public abstract class Action {
    protected Action(Map<String, String> params) {
        unpackParams(params);
    }

    // TODO: Add parse exception
    public abstract void unpackParams(Map<String, String> params);
    public abstract ExecutionRS perform();
}
