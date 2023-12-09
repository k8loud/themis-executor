package org.k8loud.executor.actions.openstack;

import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.CustomException;
import org.k8loud.executor.model.Params;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class GetServerNamesAction extends OpenstackAction {
    private String region;
    private String namePattern;

    public GetServerNamesAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public GetServerNamesAction(OpenstackService openstackService,
                                String region, String namePattern) {
        super(openstackService);
        this.region = region;
        this.namePattern = namePattern;
    }

    @Override
    public void unpackParams(Params params) throws ActionException {
        this.region = params.getRequiredParam("region");
        this.namePattern = params.getOptionalParam("namePattern", ".*");
    }

    @Override
    protected Map<String, String> executeBody() throws CustomException {
        return openstackService.getServerNames(region, namePattern);
    }
}
