package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.springframework.retry.annotation.Retryable;

import java.util.function.Function;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackNovaService {
    void resize(Server server, Flavor newFlavor, OSClient.OSClientV3 client) throws OpenstackException;

    void waitForServerStatus(Server server, Server.Status status, int waitingTimeSec, OSClient.OSClientV3 client);

    void confirmResize(Server server, OSClient.OSClientV3 client) throws OpenstackException;

    Server getServer(String serverId, OSClient.OSClientV3 client) throws OpenstackException;

    Flavor getFlavor(String flavourId, OSClient.OSClientV3 client) throws OpenstackException;

    void createServer(String name, String flavorId, String imageId, int waitActiveMillis, OSClient.OSClientV3 client,
                      Function<ServerCreateBuilder, ServerCreate> optionalSetup);

    void createServer(String name, String flavorId, String imageId, int waitActiveMillis, OSClient.OSClientV3 client);
}
