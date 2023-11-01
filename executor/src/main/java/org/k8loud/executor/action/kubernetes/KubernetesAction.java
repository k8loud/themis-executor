package org.k8loud.executor.action.kubernetes;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.action.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

@Slf4j
public abstract class KubernetesAction extends Action {
    protected KubernetesService kubernetesService;

    protected KubernetesAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params);
        this.kubernetesService = kubernetesService;
    }

    @Override
    public ExecutionRS perform() {
        String result;
        try {
            result = performKubernetesAction();
        } catch (KubernetesException e) {
            log.error("Error: {}" , e.toString());
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }
        log.info("Result: {}", result);
        return ExecutionRS.builder()
                .result(result)
                .exitCode(ExecutionExitCode.OK)
                .build();
    }

    protected abstract String performKubernetesAction() throws KubernetesException;
}
