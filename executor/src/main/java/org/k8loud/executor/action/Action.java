package org.k8loud.executor.action;


import data.ExecutionRS;
import lombok.Data;

import java.util.Map;

@Data
public abstract class Action {
    protected final Map<String, String> params;

    public abstract ExecutionRS perform();
}
