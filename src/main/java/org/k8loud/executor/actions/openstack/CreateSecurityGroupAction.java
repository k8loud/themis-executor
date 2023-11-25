package org.k8loud.executor.actions.openstack;

import lombok.EqualsAndHashCode;
import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

@EqualsAndHashCode
public class CreateSecurityGroupAction extends OpenstackAction {
    private String region;
    private String name;
    private String description;

    public CreateSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public CreateSecurityGroupAction(OpenstackService openstackService,
                                     String region, String name, String description) {
        super(openstackService);
        this.region = region;
        this.name = name;
        this.description = description;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        name = params.getRequiredParam("name");
        description = params.getRequiredParam("description");
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException, ValidationException {
        return openstackService.createSecurityGroup(region, name, description);
    }
}
