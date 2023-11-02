package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class DetachVolumeAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String volumeId;

    public DetachVolumeAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        volumeId = params.getRequiredParam("volumeId");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return this.openstackService.detachVolume(region, serverId, volumeId);
    }
}
