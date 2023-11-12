package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class RemoveSecurityGroupFromInstanceAction extends OpenstackAction {
    private String region;
    private String securityGroupId;
    private String serverId;

    public RemoveSecurityGroupFromInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupId = params.getRequiredParam("securityGroupId");
        serverId = params.getRequiredParam("serverId");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.removeSecurityGroupFromInstance(region, securityGroupId, serverId);
    }

}
