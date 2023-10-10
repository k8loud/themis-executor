package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class VerticalScalingAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String flavorId;

    public VerticalScalingAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        flavorId = params.getRequiredParam("flavorId");
    }

    @Override
    protected void performOpenstackAction() throws OpenstackException {
        this.openstackService.resizeServer(region, serverId, flavorId);
        //FIXME It is not working when resizing down -> check if root disks are same for both flavors before resizing down
    }
}
