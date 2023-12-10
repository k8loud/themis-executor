package org.k8loud.executor.actions;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.exception.ParamNotFoundException;
import org.k8loud.executor.exception.code.ActionExceptionCode;
import org.k8loud.executor.model.ExecutionExitCode;
import org.k8loud.executor.model.ExecutionRS;
import org.k8loud.executor.model.Params;

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
        Map<String, Object> resultMap;
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
        String result = (String) resultMap.remove("result");
        return ExecutionRS.builder()
                .result(result)
                .exitCode(ExecutionExitCode.OK)
                .additionalData(resultMap)
                .build();
    }

    protected abstract Map<String, Object> executeBody() throws CustomException;
}
