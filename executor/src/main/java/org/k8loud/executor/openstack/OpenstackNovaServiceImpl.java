package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.code.OpenstackExceptionCode;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.common.Buildable;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.compute.builder.ServerCreateBuilder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
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
            throw new OpenstackException(RESIZE_SERVER_FAILED,
                    "Failed to resize server with name %s. Reason: %s", server.getName(), response.getFault());
        }
    }

    @Override
    public void waitForServerStatus(Server server, Server.Status status, int waitingTimeSec,
                                    OSClient.OSClientV3 client) {
        log.debug("Waiting for server {} status \"{}\" for maximally {} sec", server.getName(), status, waitingTimeSec);
        serverConditionalWait(server, s -> s.getStatus() == status, waitingTimeSec, client);
    }

    @Override
    public void waitForServerTaskEnd(Server server, int waitingTimeSec, OSClient.OSClientV3 client) {
        log.debug("Waiting for server {} task state \"null\" for maximally {} sec", server.getName(), waitingTimeSec);
        serverConditionalWait(server, s -> Objects.isNull(s.getTaskState()), waitingTimeSec, client);
    }

    @Override
    public void confirmResize(Server server, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Confirming resizing server {}", server.getName());
        ActionResponse response = client.compute().servers().confirmResize(server.getId());
        if (!response.isSuccess()) {
            throw new OpenstackException(RESIZE_SERVER_FAILED,
                    "Failed to confirm resize server with name %s. Reason: %s", server.getName(), response.getFault());
        }
    }

    @Override
    public Server getServer(String serverId, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting server object from serverID {}", serverId);
        Server server = client.compute().servers().get(serverId);
        if (server == null) {
            throw new OpenstackException(SERVER_NOT_EXISTS, "Failed to find server with id '%s'", serverId);
        }
        return server;
    }

    @Override
    public Flavor getFlavor(String flavorId, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting flavor object from flavorID {}", flavorId);
        Flavor flavor = client.compute().flavors().get(flavorId);
        if (flavor == null) {
            throw new OpenstackException(FLAVOR_NOT_EXITS, "Failed to find flavor with id '%s'", flavorId);
        }
        return flavor;
    }

    @Override
    public void basicServerAction(Server server, Action action, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Perform action {} on server {}", action.name(), server.getName());
        OpenstackExceptionCode code = OpenstackExceptionCode.getNovaExceptionCode(action);
        ActionResponse response = client.compute().servers().action(server.getId(), action);
        if (!response.isSuccess()) {
            throw new OpenstackException(code, "Failed to perform action %s on server %s. Reason: %s",
                    action.name(), server.getName(), response.getFault());
        }
    }

    @Override
    public void createServerSnapshot(Server server, String snapshotName,
                                     OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Creating snapshot with name {} on server {}", snapshotName, server.getName());
        String snapshotId = client.compute().servers().createSnapshot(server.getId(), snapshotName);

        if (snapshotId == null) {
            throw new OpenstackException(String.format("Failed to create snapshot with name \"%s\" on server %s",
                    snapshotName, server.getName()), CREATE_SERVER_SNAPSHOT_FAILED);
        }
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
                .image(imageId).keypairName("default");

        ServerCreate serverCreate = optionalSetup.apply(serverCreateBuilder);

        int waitActive = (int) Duration.ofSeconds(waitingTimeSec).toMillis();
        Server server = client.compute().servers().bootAndWaitActive(serverCreate, waitActive);

        if (server.getStatus() != ACTIVE) {
            log.warn("Server {} not active after {}s after boot", server.getName(), waitingTimeSec);
        }
    }

    private void serverConditionalWait(Server server, Function<Server, Boolean> condition, long waitingTimeSec,
                                       OSClient.OSClientV3 client) {
        Instant start = Instant.now();
        while (isTimeNotExceeded(waitingTimeSec, start)) {
            try {
                server = getUpdatedServerObject(server, client);
                if (condition.apply(server)) {
                    break;
                }

                Thread.sleep(1000);
            } catch (ConnectionException e) {
                log.debug("Connection with openstack lost during serverConditionalWait on server {}. ", server.getName(), e);
            } catch (Exception e) {
                log.debug("Problem during serverConditionalWait on server {}. ", server.getName(), e);
            }
        }

        log.debug("Waited {} sec during serverConditionalWait on server {} (max={} sec) ",
                getPassedSeconds(start), server.getName(), waitingTimeSec);
    }

    private Server getUpdatedServerObject(Server server, OSClient.OSClientV3 client) {
        return client.compute().servers().get(server.getId());
    }

    private long getPassedSeconds(Instant start) {
        return Duration.between(start, Instant.now()).toSeconds();
    }

    private boolean isTimeNotExceeded(long waitingTimeSec, Instant start) {
        return start.plus(waitingTimeSec, ChronoUnit.SECONDS).isAfter(Instant.now());
    }
}
