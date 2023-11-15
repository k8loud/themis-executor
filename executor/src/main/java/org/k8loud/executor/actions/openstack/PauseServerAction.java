package org.k8loud.executor.actions.openstack;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class PauseServerAction extends OpenstackAction {
    private String region;
    private String serverId;

    public PauseServerAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public PauseServerAction(OpenstackService openstackService,
                             String region, String serverId) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return openstackService.pauseServer(region, serverId);
    }
}
