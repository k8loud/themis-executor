package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.retry.annotation.Retryable;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackCinderService {
    void attachVolume(Server server, Volume volume, String device, OSClient.OSClientV3 client) throws OpenstackException;

    void detachVolume(Server server, Volume volume, OSClient.OSClientV3 client) throws OpenstackException;

    Volume getVolume(String flavourId, OSClient.OSClientV3 client) throws OpenstackException;
}
