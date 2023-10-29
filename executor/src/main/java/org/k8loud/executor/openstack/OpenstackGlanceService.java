package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Server;
import org.springframework.retry.annotation.Retryable;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackGlanceService {
    void deleteTheOldestSnapshot(Server server, boolean keepOneSnapshot, OSClient.OSClientV3 client) throws OpenstackException;
}
