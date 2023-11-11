package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.springframework.stereotype.Service;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;

@Service
@Slf4j
public class OpenstackNeutronServiceImpl implements OpenstackNeutronService {

    @Override
    public void createSecurityGroup(String name, String description,
                                    OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Creating security group with name '{}' and description '{}'", name, description);
        SecurityGroup securityGroup = client.networking().securitygroup().create(Builders.securityGroup()
                .name(name)
                .description(description)
                .build());

        if (securityGroup == null) {
            throw new OpenstackException(CREATE_SECURITY_GROUP_FAILED,
                    "Failed to create SecurityGroup with name \"%s\"", name);
        }
    }

    @Override
    public SecurityGroup getSecurityGroup(String securityGroupId,
                                          OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting security group object with id '{}'", securityGroupId);
        SecurityGroup securityGroup = client.networking().securitygroup().get(securityGroupId);

        if (securityGroup == null) {
            throw new OpenstackException(SECURITY_GROUP_NOT_EXIST,
                    "Failed to find SecurityGroup with '%s'", securityGroupId);
        }

        return securityGroup;
    }

    @Override
    public SecurityGroupRule createSecurityGroupRule(SecurityGroup securityGroup, String ethertype, String direction,
                                                     String remoteIpPrefix, String protocol, int portRangeMin,
                                                     int portRangeMax,
                                                     OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Creating new rule for SecurityGroup with id '{}'", securityGroup.getName());
        SecurityGroupRule securityGroupRule = client.networking().securityrule().create(Builders.securityGroupRule()
                .securityGroupId(securityGroup.getId())
                .direction(direction)
                .ethertype(ethertype)
                .portRangeMin(portRangeMin)
                .portRangeMax(portRangeMax)
                .protocol(protocol)
                .remoteIpPrefix(remoteIpPrefix)
                .build());

        if (securityGroupRule == null) {
            throw new OpenstackException(ADD_RULE_FAILED,
                    "Failed to add rule to SecurityGroup '%s'", securityGroup.getName());
        }

        return securityGroupRule;
    }
}
