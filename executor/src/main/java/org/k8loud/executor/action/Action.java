package org.k8loud.executor.action;


import data.ExecutionRS;
import lombok.Data;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.code.ActionExceptionCode;

import java.util.Map;

@Data
public abstract class Action {
    protected Action(Map<String, String> params) throws ActionException {
        try {
            unpackParams(params);
        } catch (Exception e) {
            throw new ActionException(e, ActionExceptionCode.UNPACKING_PARAMS_FAILURE);
        }
    }

    public abstract void unpackParams(Map<String, String> params) throws ActionException;
    public abstract ExecutionRS perform();
}
