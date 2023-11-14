package org.k8loud.executor.actions.openstack;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class RemoveRuleFromSecurityGroupAction extends OpenstackAction {
    private String region;
    private String securityGroupRuleId;

    public RemoveRuleFromSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public RemoveRuleFromSecurityGroupAction(OpenstackService openstackService,
                                             String region, String securityGroupRuleId) {
        super(openstackService);
        this.region = region;
        this.securityGroupRuleId = securityGroupRuleId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupRuleId = params.getRequiredParam("securityGroupRuleId");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return openstackService.removeSecurityGroupRule(region, securityGroupRuleId);
    }

}
