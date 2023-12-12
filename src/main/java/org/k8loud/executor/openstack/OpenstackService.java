package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;

import java.util.List;
import java.util.Map;

//FIXME enable annotation here not in implementation (with spring-retry working)
public interface OpenstackService {
    Map<String, Object> resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException;

    Map<String, Object> resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException;

    Map<String, Object> getServerNames(String region, String namePattern) throws OpenstackException, ValidationException;

    Map<String, Object> createServers(String region, String name, String imageId, String flavorId, String keypairName,
                         String securityGroup, String userData, int count, int waitActiveSec) throws OpenstackException, ValidationException;

    Map<String, Object> deleteServers(String region, String namePattern) throws OpenstackException, ValidationException;

    Map<String, Object> deleteServers(String region, List<String> serverIds) throws OpenstackException, ValidationException;

    Map<String, Object> attachVolume(String region, String serverId, String volumeId, String device) throws OpenstackException;

    Map<String, Object> detachVolume(String region, String serverId, String volumeId) throws OpenstackException;

    Map<String, Object> pauseServer(String region, String serverId) throws OpenstackException;

    Map<String, Object> unpauseServer(String region, String serverId) throws OpenstackException;

    Map<String, Object> createServerSnapshot(String region, String serverId, String snapshotName,
                                boolean stopInstance) throws OpenstackException, ValidationException;

    Map<String, Object> deleteTheOldestServerSnapshot(String region, String serverId,
                                         boolean keepOneSnapshot) throws OpenstackException, ValidationException;

    Map<String, Object> createVolumeSnapshot(String region, String volumeId, String snapshotName) throws OpenstackException, ValidationException;

    Map<String, Object> deleteTheOldestVolumeSnapshot(String region, String volumeId,
                                         boolean keepOneSnapshot) throws OpenstackException, ValidationException;

    Map<String, Object> createSecurityGroup(String region, String name, String description) throws OpenstackException, ValidationException;
    Map<String, Object> removeSecurityGroup(String region, String securityGroupId) throws OpenstackException;

    Map<String, Object> addSecurityGroupToInstance(String region, String securityGroupId, String serverId) throws OpenstackException;

    Map<String, Object> removeSecurityGroupFromInstance(String region, String securityGroupId, String serverId) throws OpenstackException;

    Map<String, Object> addRuleToSecurityGroup(String region, String securityGroupId, String ethertype, String direction,
                                  String remoteIpPrefix, String protocol, int portRangeMin,
                                  int portRangeMax, String description) throws OpenstackException, ValidationException;

    Map<String, Object> removeSecurityGroupRule(String region, String securityGroupRuleId) throws OpenstackException;

    Map<String, Object> throttle(String region, String serverId, String ethertype,
                                 String remoteIpPrefix, String protocol, int portRangeMin,
                                 int portRangeMax, long secDuration) throws OpenstackException, ValidationException;
}
