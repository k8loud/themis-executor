package org.k8loud.executor.action.openstack;

import org.k8loud.executor.model.Params;
import lombok.Builder;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.openstack.OpenstackService;

import java.util.Map;

public class IpThrottleAction extends OpenstackAction {
    private String region;
    private String serverId;
    private String ethertype; //IPv4 or IPv6
    private String remoteIpPrefix; //example 0.0.0.0/0
    private String protocol; //example TCP
    private int portRangeMin;
    private int portRangeMax;
    private long secDuration;

    public IpThrottleAction(Params params, OpenstackService openstackService) throws ActionException {
        super(params, openstackService);
    }

    @Builder
    public IpThrottleAction(OpenstackService openstackService, String region, String serverId, String ethertype,
                            String direction, String remoteIpPrefix, String protocol, int portRangeMin,
                            int portRangeMax, int secDuration) {
        super(openstackService);
        this.region = region;
        this.serverId = serverId;
        this.ethertype = ethertype;
        this.remoteIpPrefix = remoteIpPrefix;
        this.protocol = protocol;
        this.portRangeMin = portRangeMin;
        this.portRangeMax = portRangeMax;
        this.secDuration = secDuration;
    }

    @Override
    public void unpackParams(Params params) {
        region = params.getRequiredParam("region");
        serverId = params.getRequiredParam("serverId");
        ethertype = params.getRequiredParam("ethertype");
        remoteIpPrefix = params.getRequiredParam("remoteIpPrefix");
        protocol = params.getRequiredParam("protocol");
        portRangeMin = params.getRequiredParamAsInteger("portRangeMin");
        portRangeMax = params.getRequiredParamAsInteger("portRangeMax");
        secDuration = params.getOptionalParamAsLong("secDuration", (long) (5 * 60));
    }

    @Override
    protected Map<String, String> executeBody() throws OpenstackException {
        return openstackService.throttle(region, serverId, ethertype, remoteIpPrefix, protocol, portRangeMin,
                portRangeMax, secDuration);
    }
}