package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class VerticalScalingDownAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String flavorId;

    public VerticalScalingDownAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public VerticalScalingDownAction(OpenstackService openstackService,
                                     String region, String serverId, String flavorId) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.flavorId = flavorId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        flavorId = params.getRequiredParam("flavorId");
    }

    @Override
    protected Map<String, Object> executeBody() throws OpenstackException {
        return this.openstackService.resizeServerDown(region, serverId, flavorId);
    }
}
