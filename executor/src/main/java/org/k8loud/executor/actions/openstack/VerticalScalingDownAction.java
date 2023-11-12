package org.k8loud.executor.actions.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class VerticalScalingDownAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String flavorId;

    public VerticalScalingDownAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        flavorId = params.getRequiredParam("flavorId");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return this.openstackService.resizeServerDown(region, serverId, flavorId);
    }
}
