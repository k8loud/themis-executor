package org.k8loud.executor.actions.openstack;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class DetachVolumeAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String volumeId;

    public DetachVolumeAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public DetachVolumeAction(OpenstackService openstackService,
                              String region, String serverId, String volumeId) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.volumeId = volumeId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        volumeId = params.getRequiredParam("volumeId");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return this.openstackService.detachVolume(region, serverId, volumeId);
    }
}
