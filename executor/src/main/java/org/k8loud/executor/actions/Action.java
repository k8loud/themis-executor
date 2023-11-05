package org.k8loud.executor.actions;


import data.ExecutionRS;
import data.Params;
import exception.ParamNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.code.ActionExceptionCode;

@Data
@NoArgsConstructor
public abstract class Action {
    protected Action(Params params) throws ActionException {
        try {
            unpackParams(params);
        } catch (ParamNotFoundException e) {
            throw new ActionException(e, ActionExceptionCode.UNPACKING_PARAMS_FAILURE);
        }
    }

    public abstract void unpackParams(Params params) throws ActionException;

    public abstract ExecutionRS perform();
}
