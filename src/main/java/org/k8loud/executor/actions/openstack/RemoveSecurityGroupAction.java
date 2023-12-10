package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class RemoveSecurityGroupAction extends OpenstackAction {
    private String region;
    private String securityGroupId;

    public RemoveSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public RemoveSecurityGroupAction(OpenstackService openstackService, String region, String securityGroupId) {
        super(openstackService);
        this.region = region;
        this.securityGroupId = securityGroupId;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupId = params.getRequiredParam("securityGroupId");
    }

    @Override
    protected Map<String, Object> executeBody() throws OpenstackException {
        return openstackService.removeSecurityGroup(region, securityGroupId);
    }

}
