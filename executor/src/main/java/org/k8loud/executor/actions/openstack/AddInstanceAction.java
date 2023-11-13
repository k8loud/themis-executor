package org.k8loud.executor.actions.openstack;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class AddInstanceAction extends OpenstackAction {
    private String region;
    private String name;
    private String imageId;
    private String flavorId;
    private String keypairName;
    private String securityGroup;
    private String userData;
    private int count;
    private int waitActiveSec;

    public AddInstanceAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public AddInstanceAction(OpenstackService openstackService,
                             String region, String name, String imageId, String flavorId, String keypairName,
                             String securityGroup, String userData, int count, int waitActiveSec) {
        super(openstackService);
        this.region = region;
        this.name = name;
        this.imageId = imageId;
        this.flavorId = flavorId;
        this.keypairName = keypairName;
        this.securityGroup = securityGroup;
        this.userData = userData;
        this.count = count;
        this.waitActiveSec = waitActiveSec;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        name = params.getRequiredParam("name");
        imageId = params.getRequiredParam("imageId");
        flavorId = params.getRequiredParam("flavorId");
        keypairName = params.getOptionalParam("keypairName", "default");
        securityGroup = params.getOptionalParam("securityGroup", null);
        userData = params.getOptionalParam("userData", null);
        count = params.getOptionalParamAsInt("count", 1);
        waitActiveSec = params.getOptionalParamAsInt("waitActiveSec", 300);
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.createServers(region, name, imageId, flavorId, keypairName, securityGroup, userData,
                count, waitActiveSec);
    }

}
