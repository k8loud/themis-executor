package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.code.OpenstackExceptionCode;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.springframework.retry.annotation.Retryable;

import java.util.function.Function;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackNovaService {
    void resize(Server server, Flavor newFlavor, OSClient.OSClientV3 client) throws OpenstackException;

    void confirmResize(Server server, OSClient.OSClientV3 client) throws OpenstackException;

    void createServer(String name, String flavorId, String imageId, int waitActiveMillis, OSClient.OSClientV3 client,
                      Function<ServerCreateBuilder, ServerCreate> optionalSetup);

    void createServer(String name, String flavorId, String imageId, int waitActiveMillis, OSClient.OSClientV3 client);

    Server getServer(String serverId, OSClient.OSClientV3 client) throws OpenstackException;

    Flavor getFlavor(String flavourId, OSClient.OSClientV3 client) throws OpenstackException;

    void basicServerAction(Server server, Action action, OSClient.OSClientV3 client) throws OpenstackException;

    void createServerSnapshot(Server server, String snapshotName, OSClient.OSClientV3 client) throws OpenstackException;

    @Retryable(maxAttempts = 1)
    void waitForServerStatus(Server server, Server.Status status, int waitingTimeSec, OSClient.OSClientV3 client);

    @Retryable(maxAttempts = 1)
    void waitForServerTaskEnd(Server server, int waitingTimeSec, OSClient.OSClientV3 client);
}
