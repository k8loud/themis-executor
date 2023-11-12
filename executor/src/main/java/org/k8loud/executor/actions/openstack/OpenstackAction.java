package org.k8loud.executor.actions.openstack;

import data.Params;
import org.k8loud.executor.actions.Action;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.openstack.OpenstackService;

public abstract class OpenstackAction extends Action {
    protected OpenstackService openstackService;

    protected OpenstackAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params);
        this.openstackService = openstackService;
    }
}
