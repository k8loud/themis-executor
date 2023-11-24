package org.k8loud.executor.actions;


import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import exception.ParamNotFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.code.ActionExceptionCode;

import java.util.Map;

@Slf4j
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

    public ExecutionRS execute() {
        Map<String, String> resultMap;
        try {
            resultMap = executeBody();
        } catch (CustomException e) {
            log.error("Error: {}" , e.toString());
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }
        log.info("Result: {} [{}]", getClass().getName(), resultMap);
        String result = resultMap.remove("result");
        return ExecutionRS.builder()
                .result(result)
                .exitCode(ExecutionExitCode.OK)
                .additionalData(resultMap)
                .build();
    }

    protected abstract Map<String,String> executeBody() throws CustomException;
}
