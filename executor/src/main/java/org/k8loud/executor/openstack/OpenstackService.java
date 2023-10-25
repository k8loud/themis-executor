package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;

//FIXME enable annotation here not in implementation (with spring-retry working)
public interface OpenstackService {
    void resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException;

    void resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException;

    void copyServer(String region, String serverId) throws OpenstackException;

    void attachVolume(String region, String serverId, String volumeId, String device) throws OpenstackException;

    void detachVolume(String region, String serverId, String volumeId) throws OpenstackException;

    void pauseServer(String region, String serverId) throws OpenstackException;

    void unpauseServer(String region, String serverId) throws OpenstackException;

    void createServerSnapshot(String region, String serverId, String snapshotName, boolean stopInstance) throws OpenstackException;

    void deleteTheOldestServerSnapshot(String region, String serverId, boolean keepOneSnapshot) throws OpenstackException;
}
