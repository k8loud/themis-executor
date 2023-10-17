package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class UnpauseServerAction extends OpenstackAction {
    private String region;
    private String serverId;

    public UnpauseServerAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
    }

    @Override
    protected void performOpenstackAction() throws OpenstackException {
        openstackService.unpauseServer(region, serverId);
    }
}
