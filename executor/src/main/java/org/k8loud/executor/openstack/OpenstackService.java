package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;

import java.util.Map;

//FIXME enable annotation here not in implementation (with spring-retry working)
public interface OpenstackService {
    Map<String, String> resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException;

    Map<String, String> resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException;

    Map<String, String> createServers(String region, String name, String imageId, String flavorId, String keypairName,
                         String securityGroup, String userData, int count, int waitActiveSec) throws OpenstackException, ValidationException;

    Map<String, String> attachVolume(String region, String serverId, String volumeId, String device) throws OpenstackException;

    Map<String, String> detachVolume(String region, String serverId, String volumeId) throws OpenstackException;

    Map<String, String> pauseServer(String region, String serverId) throws OpenstackException;

    Map<String, String> unpauseServer(String region, String serverId) throws OpenstackException;

    Map<String, String> createServerSnapshot(String region, String serverId, String snapshotName,
                                boolean stopInstance) throws OpenstackException, ValidationException;

    Map<String, String> deleteTheOldestServerSnapshot(String region, String serverId,
                                         boolean keepOneSnapshot) throws OpenstackException;

    Map<String, String> createVolumeSnapshot(String region, String volumeId, String snapshotName) throws OpenstackException;

    Map<String, String> deleteTheOldestVolumeSnapshot(String region, String volumeId,
                                         boolean keepOneSnapshot) throws OpenstackException;

    Map<String, String> createSecurityGroup(String region, String name, String description) throws OpenstackException, ValidationException;

    Map<String, String> addSecurityGroupToInstance(String region, String securityGroupId, String serverId) throws OpenstackException;

    Map<String, String> removeSecurityGroupFromInstance(String region, String securityGroupId, String serverId) throws OpenstackException;

    Map<String, String> addRuleToSecurityGroup(String region, String securityGroupId, String ethertype, String direction,
                                  String remoteIpPrefix, String protocol, int portRangeMin,
                                  int portRangeMax, String description) throws OpenstackException, ValidationException;

    Map<String, String> removeSecurityGroupRule(String region, String securityGroupRuleId) throws OpenstackException;
}
