package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.common.Buildable;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;
import static org.openstack4j.model.compute.Server.Status.ACTIVE;

@Service
@Slf4j
public class OpenstackNovaServiceImpl implements OpenstackNovaService {
    @Override
    public void resize(Server server, Flavor newFlavor, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Resizing server {} to flavor {}", server.getName(), newFlavor.getId());
        ActionResponse response = client.compute().servers().resize(server.getId(), newFlavor.getId());
        if (!response.isSuccess()) {
            log.error("Failed to resize server (id={}). Reason: {}", server.getId(), response.getFault());
            throw new OpenstackException(response.getFault(), RESIZE_SERVER_FAILED);
        }
    }

    @Override
    public void waitForServerStatus(Server server, Server.Status status, int waitingTimeSec,
                                    OSClient.OSClientV3 client) {
        log.debug("Waiting for server {} status {} for maximally {} sec", server.getName(), status, waitingTimeSec);
        client.compute().servers().waitForServerStatus(server.getId(), status, waitingTimeSec, TimeUnit.SECONDS);
    }

    @Override
    public void confirmResize(Server server, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Confirming resizing server {}", server.getName());
        ActionResponse response = client.compute().servers().confirmResize(server.getId());
        if (!response.isSuccess()) {
            log.error("Failed to confirm resize server (id={}). Reason: {}", server.getId(), response.getFault());
            throw new OpenstackException(response.getFault(), RESIZE_SERVER_FAILED);
        }
    }

    @Override
    public Server getServer(String serverId, OSClient.OSClientV3 client) throws OpenstackException {
        log.trace("Getting server object from serverID {}", serverId);
        Server server = client.compute().servers().get(serverId);
        if (server == null) {
            throw new OpenstackException(SERVER_NOT_EXISTS);
        }
        return server;
    }

    @Override
    public Flavor getFlavor(String flavourId, OSClient.OSClientV3 client) throws OpenstackException {
        log.trace("Getting flavor object from flavorID {}", flavourId);
        Flavor flavor = client.compute().flavors().get(flavourId);
        if (flavor == null) {
            throw new OpenstackException(FLAVOR_NOT_EXITS);
        }
        return flavor;
    }

    @Override
    public void createServer(String name, String flavorId, String imageId, int waitingTimeSec,
                             OSClient.OSClientV3 client, Function<ServerCreateBuilder, ServerCreate> optionalSetup) {
        createNewServer(name, flavorId, imageId, waitingTimeSec, client, optionalSetup);
    }

    @Override
    public void createServer(String name, String flavorId, String imageId, int waitingTimeSec,
                             OSClient.OSClientV3 client) {
        createNewServer(name, flavorId, imageId, waitingTimeSec, client, Buildable.Builder::build);
    }

    private void createNewServer(String name, String flavorId, String imageId, int waitingTimeSec,
                                 OSClient.OSClientV3 client,
                                 Function<ServerCreateBuilder, ServerCreate> optionalSetup) {
        log.debug("Creating new server. Name={}, flavorID={}, imageID={}", name, flavorId, imageId);
        ServerCreateBuilder serverCreateBuilder = Builders.server().name(name).flavor(flavorId)
                .image(imageId).keypairName("default-from-api");

        ServerCreate serverCreate = optionalSetup.apply(serverCreateBuilder);

        int waitActive = (int) Duration.ofSeconds(waitingTimeSec).toMillis();
        Server server = client.compute().servers().bootAndWaitActive(serverCreate, waitActive);

        if (server.getStatus() != ACTIVE) {
            log.warn("Server {} not active after {}s after boot", server.getName(), waitingTimeSec);
        }
    }
}
