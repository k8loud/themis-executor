package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;

//FIXME enable annotation here not in implementation (with spring-retry working)
public interface OpenstackService {
    String resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException;

    String resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException;

    String createServers(String region, String name, String imageId, String flavorId, String keypairName,
                         String securityGroup, String userData, int count, int waitActiveSec) throws OpenstackException;

    String attachVolume(String region, String serverId, String volumeId, String device) throws OpenstackException;

    String detachVolume(String region, String serverId, String volumeId) throws OpenstackException;

    String pauseServer(String region, String serverId) throws OpenstackException;

    String unpauseServer(String region, String serverId) throws OpenstackException;

    String createServerSnapshot(String region, String serverId, String snapshotName,
                                boolean stopInstance) throws OpenstackException;

    String deleteTheOldestServerSnapshot(String region, String serverId,
                                         boolean keepOneSnapshot) throws OpenstackException;

    String createVolumeSnapshot(String region, String volumeId, String snapshotName) throws OpenstackException;

    String deleteTheOldestVolumeSnapshot(String region, String volumeId,
                                         boolean keepOneSnapshot) throws OpenstackException;

    String createSecurityGroup(String region, String name, String description) throws OpenstackException;
    String addSecurityGroupToInstance(String region, String securityGroupId, String serverId) throws OpenstackException;
}
