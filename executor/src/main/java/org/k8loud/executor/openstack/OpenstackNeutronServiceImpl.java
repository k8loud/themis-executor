package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.Util;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;

@Service
@Slf4j
public class OpenstackNeutronServiceImpl implements OpenstackNeutronService {

    @Override
    public SecurityGroup createSecurityGroup(String name, String description,
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
        return securityGroup;
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
    public SecurityGroupRule addSecurityGroupRule(SecurityGroup securityGroup, String ethertype, String direction,
                                                  String remoteIpPrefix, String protocol, int portRangeMin,
                                                  int portRangeMax, String description,
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
                .description(description)
                .build());

        if (securityGroupRule == null) {
            throw new OpenstackException(ADD_RULE_FAILED,
                    "Failed to add rule to SecurityGroup '%s'", securityGroup.getName());
        }

        return securityGroupRule;
    }

    @Override
    public Set<SecurityGroupRule> modifySecurityGroupsDuringThrottling(
            Map<SecurityGroup, Set<SecurityGroupRule>> rulesToModify, String remoteIpPrefix, int portRangeMin,
            int portRangeMax, Supplier<OSClient.OSClientV3> clientSupplier) throws OpenstackException {

        Set<SecurityGroupRule> throttlingRules = Collections.synchronizedSet(new HashSet<>());
        rulesToModify.keySet().stream().parallel().forEach(group -> {
            rulesToModify.get(group).stream().parallel().forEach(rule -> {
                String subnet = rule.getRemoteIpPrefix();
                Util.getDifferenceCIDRS(subnet, remoteIpPrefix).forEach(cidr -> {
                    try {
                        //TODO: for now, we don care about ports. Should recreate current rule without ports range
                        // defined in action, and create throttling rule for defined range
                        throttlingRules.add(
                                addSecurityGroupRule(group, rule.getEtherType(), rule.getDirection(),
                                        cidr.getInfo().getCidrSignature(), rule.getProtocol(), rule.getPortRangeMin(),
                                        rule.getPortRangeMax(), null, clientSupplier.get()));
                    } catch (OpenstackException e) {
                        log.error(e.toString());
                    }
                });

                removeSecurityGroupRule(rule, clientSupplier.get());
            });
        });

        return throttlingRules;
    };

    @Override
    public Map<SecurityGroup, Set<SecurityGroupRule>> getThrottlingRules(Server server, String ethertype,
                                                                         String remoteIpPrefix, String protocol,
                                                                         int portRangeMin, int portRangeMax,
                                                                         OSClient.OSClientV3 client) throws OpenstackException {
        Map<SecurityGroup, Set<SecurityGroupRule>> throttlingRules = new HashMap<>();
        Set<String> serverGroupsNames = server.getSecurityGroups()
                .stream()
                .map(org.openstack4j.model.compute.SecurityGroup::getName)
                .collect(Collectors.toSet());

        client.networking().securitygroup().list().stream()
                .filter(group -> serverGroupsNames.contains(group.getName()))
                .forEach(group -> {
                    Set<SecurityGroupRule> matchedRules = group.getRules().stream()
                            .filter(rule -> checkThrottlingRule(rule, ethertype, remoteIpPrefix, protocol, portRangeMin,
                                    portRangeMax))
                            .collect(Collectors.toSet());

                    if (!matchedRules.isEmpty()) {
                        throttlingRules.put(group, matchedRules);
                    }
                });

        if (throttlingRules.isEmpty()) {
            throw new OpenstackException(IP_THROTTLE_FAILED, "Provided rule settings are already denied in server %s",
                    server.getName());
        }
        return throttlingRules;
    }

    @Override
    public void removeSecurityGroupRule(SecurityGroupRule securityGroupRule, OSClient.OSClientV3 client) {
        log.debug("Deleting security group rule ({})", securityGroupRule.toString());
        client.networking().securityrule().delete(securityGroupRule.getId());
    }

    @Override
    public void removeSecurityGroup(SecurityGroup securityGroup, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Deleting security group ({})", securityGroup.getName());
        ActionResponse response = client.networking().securitygroup().delete(securityGroup.getId());

        if (!response.isSuccess()) {
            throw new OpenstackException(REMOVE_SECURITY_GROUP_FAILED,
                    "Failed to delete security group named %s. Reason: %s", securityGroup.getName(), response.getFault());
        }
    }

    @Override
    public SecurityGroupRule getSecurityGroupRule(String securityGroupRuleId, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting security group rule object with id '{}'", securityGroupRuleId);
        SecurityGroupRule securityGroupRule = client.networking().securityrule().get(securityGroupRuleId);

        if (securityGroupRule == null) {
            throw new OpenstackException(SECURITY_GROUP_RULE_NOT_EXIST,
                    "Failed to find SecurityGroupRule with '%s'", securityGroupRuleId);
        }

        return securityGroupRule;
    }

    private boolean checkThrottlingRule(SecurityGroupRule rule, String ethertype, String remoteIpPrefix,
                                        String protocol, int portRangeMin, int portRangeMax) {
        //TODO: for now, we don care about ports. In Future: uncomment port condition and change logic in
        // getThrottlingRules to use specific port ranges
        log.info(rule.toString());
        Stream<Boolean> conditions = Stream.of(
                rule.getDirection().equals("ingress"),
                rule.getEtherType().equals(ethertype),
                protocol.equalsIgnoreCase(rule.getProtocol()),
//                !Boolean.logicalOr(rule.getPortRangeMax() < portRangeMin, rule.getPortRangeMin() > portRangeMax),
                Util.hasCommonSubnet(rule.getRemoteIpPrefix(), remoteIpPrefix)
        );

        return conditions.allMatch(condition -> condition.equals(true));
    }
}
