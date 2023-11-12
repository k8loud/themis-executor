package org.k8loud.executor.action.openstack;

import data.Params;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class RemoveRuleFromSecurityGroupAction extends OpenstackAction {
    private String region;
    private String securityGroupRuleId;

    public RemoveRuleFromSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupRuleId = params.getRequiredParam("securityGroupRuleId");
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.removeSecurityGroupRule(region, securityGroupRuleId);
    }

}
