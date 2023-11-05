package org.k8loud.executor.actions.openstack;

import data.ExecutionExitCode;
import data.ExecutionRS;
import data.Params;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public abstract class OpenstackAction extends Action {
    protected OpenstackService openstackService;

    protected OpenstackAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params);
        this.openstackService = openstackService;
    }

    @Override
    public ExecutionRS perform() {
        try {
            performOpenstackAction();
        } catch (OpenstackException e) {
            return ExecutionRS.builder()
                    .result(e.toString())
                    .exitCode(ExecutionExitCode.NOT_OK)
                    .build();
        }

        return ExecutionRS.builder()
                .result("Success")
                .exitCode(ExecutionExitCode.OK)
                .build();
    }

    protected abstract void performOpenstackAction() throws OpenstackException;
}
