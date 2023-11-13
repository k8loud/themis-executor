package org.k8loud.executor.actions.openstack;

import data.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

public class AddRuleToSecurityGroupAction extends OpenstackAction {
    private String region;
    private String securityGroupId;
    private String ethertype; //IPv4 or IPv6
    private String direction; //ingress or egress
    private String remoteIpPrefix; //example 0.0.0.0/0
    private String protocol; //example TCP
    private int portRangeMin;
    private int portRangeMax;
    private String description;

    public AddRuleToSecurityGroupAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public AddRuleToSecurityGroupAction(OpenstackService openstackService,
                                        String region, String securityGroupId, String ethertype, String direction,
                                        String remoteIpPrefix, String protocol, int portRangeMin, int portRangeMax,
                                        String description) {
        super(openstackService);
        this.region = region;
        this.securityGroupId = securityGroupId;
        this.ethertype = ethertype;
        this.direction = direction;
        this.remoteIpPrefix = remoteIpPrefix;
        this.protocol = protocol;
        this.portRangeMin = portRangeMin;
        this.portRangeMax = portRangeMax;
        this.description = description;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        securityGroupId = params.getRequiredParam("securityGroupId");
        ethertype = params.getRequiredParam("ethertype");
        direction = params.getRequiredParam("direction");
        remoteIpPrefix = params.getRequiredParam("remoteIpPrefix");
        protocol = params.getRequiredParam("protocol");
        portRangeMin = params.getRequiredParamAsInteger("portRangeMin");
        portRangeMax = params.getRequiredParamAsInteger("portRangeMax");
        description = params.getOptionalParam("description", null);
    }

    @Override
    protected String executeBody() throws OpenstackException {
        return openstackService.addRuleToSecurityGroup(region, securityGroupId, ethertype, direction, remoteIpPrefix,
                protocol, portRangeMin, portRangeMax, description);
    }

}
