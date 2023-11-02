package org.k8loud.executor.action;


import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import exception.ParamNotFoundException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.code.ActionExceptionCode;

@Slf4j
@Data
public abstract class Action {
    protected Action(Params params) throws ActionException {
        try {
            unpackParams(params);
        } catch (ParamNotFoundException e) {
            throw new ActionException(e, ActionExceptionCode.UNPACKING_PARAMS_FAILURE);
        }
    }

    public abstract void unpackParams(Params params) throws ActionException;

    public ExecutionRS execute() {
        String result;
        try {
            result = executeBody();
        } catch (CustomException e) {
            log.error("Error: {}" , e.toString());
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }
        log.info("Result: {} [{}]", getClass().getName(), result);
        return ExecutionRS.builder()
                .result(result)
                .exitCode(ExecutionExitCode.OK)
                .build();
    }

    protected abstract String executeBody() throws CustomException;
}
