package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class AttachVolumeAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String volumeId;
    private String device;

    public AttachVolumeAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public AttachVolumeAction(OpenstackService openstackService,
                              String region, String serverId, String volumeId, String device) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.volumeId = volumeId;
        this.device = device;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        volumeId = params.getRequiredParam("volumeId");
        device = params.getRequiredParam("device");
    }

    @Override
    protected Map<String, Object> executeBody() throws OpenstackException {
        return this.openstackService.attachVolume(region, serverId, volumeId, device);
    }
}
