package org.k8loud.executor.actions.openstack;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class VerticalScalingUpAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String flavorId;

    public VerticalScalingUpAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public VerticalScalingUpAction(OpenstackService openstackService,
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
    protected Map<String, String> executeBody() throws OpenstackException {
        return this.openstackService.resizeServerUp(region, serverId, flavorId);
    }
}
