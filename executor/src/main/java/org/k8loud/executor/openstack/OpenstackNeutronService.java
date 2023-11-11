package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.springframework.retry.annotation.Retryable;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackNeutronService {
    void createSecurityGroup(String name, String description, OSClient.OSClientV3 client) throws OpenstackException;

    SecurityGroup getSecurityGroup(String securityGroupId, OSClient.OSClientV3 client) throws OpenstackException;

    SecurityGroupRule createSecurityGroupRule(SecurityGroup securityGroup, String ethertype, String direction,
                                              String remoteIpPrefix, String protocol, int portRangeMin,
                                              int portRangeMax, OSClient.OSClientV3 client) throws OpenstackException;
}
