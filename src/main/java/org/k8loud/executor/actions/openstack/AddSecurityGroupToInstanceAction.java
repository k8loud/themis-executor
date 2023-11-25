package org.k8loud.executor.actions.openstack;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class AddSecurityGroupToInstanceAction extends OpenstackAction {
    private String region;
    private String securityGroupId;
    private String serverId;

    public AddSecurityGroupToInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public AddSecurityGroupToInstanceAction(OpenstackService openstackService,
                                            String region, String securityGroupId, String serverId) {
        super(openstackService);
        this.region = region;
        this.securityGroupId = securityGroupId;
        this.serverId = serverId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupId = params.getRequiredParam("securityGroupId");
        serverId = params.getRequiredParam("serverId");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return openstackService.addSecurityGroupToInstance(region, securityGroupId, serverId);
    }

}
