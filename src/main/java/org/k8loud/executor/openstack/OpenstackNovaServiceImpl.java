package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.code.OpenstackExceptionCode;
import org.k8loud.executor.util.Util;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.compute.ServerCreate;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.network.SecurityGroup;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;

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
    public List<Server> getServers(OSClient.OSClientV3 client, Pattern namePattern) {
        return client.compute().servers().list()
                .stream()
                .filter(s -> namePattern.matcher(s.getName()).matches())
                .map(s -> (Server) s)
                .toList();
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
    public String createServerSnapshot(Server server, String snapshotName,
                                     OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Creating snapshot with name {} on server {}", snapshotName, server.getName());
        String snapshotId = client.compute().servers().createSnapshot(server.getId(), snapshotName);

        if (snapshotId == null) {
            throw new OpenstackException(String.format("Failed to create snapshot with name \"%s\" on server %s",
                    snapshotName, server.getName()), CREATE_SERVER_SNAPSHOT_FAILED);
        }

        return snapshotId;
    }

    @Override
    public void addSecurityGroupToInstance(Server server, SecurityGroup securityGroup,
                                           OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Adding SecurityGroup '{}' to Server '{}'", securityGroup.getName(), server.getName());
        ActionResponse response = client.compute().servers().addSecurityGroup(server.getId(), securityGroup.getId());
        if (!response.isSuccess()) {
            throw new OpenstackException(ADD_SECURITY_GROUP_FAILED,
                    "Failed to add SecurityGroup %s to server %s. Reason: %s",
                    securityGroup.getName(), server.getName(), response.getFault());
        }
    }

    @Override
    public void removeSecurityGroupFromInstance(Server server, SecurityGroup securityGroup,
                                                OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Removing SecurityGroup '{}' from Server '{}'", securityGroup.getName(), server.getName());
        ActionResponse response = client.compute().servers().removeSecurityGroup(server.getId(), securityGroup.getId());
        if (!response.isSuccess()) {
            throw new OpenstackException(REMOVE_SECURITY_GROUP_FROM_INSTANCE_FAILED,
                    "Failed to remove SecurityGroup %s from server %s. Reason: %s",
                    securityGroup.getName(), server.getName(), response.getFault());
        }
    }

    @Override
    public List<String> createServers(String name, Image image, Flavor flavor, String keypairName, String securityGroup,
                                             String userData, int count, int waitActiveSec,
                                             Supplier<OSClient.OSClientV3> clientSupplier) throws OpenstackException {
        log.debug("Creating servers with name prefix '{}'", name);

        List<?> results = IntStream.rangeClosed(1, count)
                .parallel()
                .mapToObj(i -> {
                    try {
                        return spawnServer(Util.nameWithUuid(name), flavor, image, keypairName,
                                securityGroup, userData, waitActiveSec, clientSupplier.get());
                    } catch (OpenstackException e) {
                        return e;
                    }
                })
                .toList();

        if (results.stream().anyMatch(s -> OpenstackException.class.isAssignableFrom(s.getClass()))) {
            throw new OpenstackException(CREATE_SERVER_FAILED,
                    "Failed to create %d servers named %s. List of results: %s", count, name, results);
        }

        return results.stream().map(s -> ((Server) s).getId()).toList();
    }

    @Override
    public List<String> deleteServers(Pattern namePattern,
                                      Supplier<OSClient.OSClientV3> clientSupplier) throws OpenstackException {
        log.debug("Deleting servers with name pattern '{}'", namePattern.pattern());

        List<Server> servers = getServers(clientSupplier.get(), namePattern);

        return parallelDeleteServers(servers, clientSupplier);
    }

    @Override
    public List<String> deleteServers(List<Server> servers,
                                      Supplier<OSClient.OSClientV3> clientSupplier) throws OpenstackException {
        log.debug("Deleting servers named '{}'", servers.stream().map(Server::getName).toList());

        return parallelDeleteServers(servers, clientSupplier);
    }

    private Server spawnServer(String name, Flavor flavor, Image image, String keypairName, String securityGroup,
                               String userData, int waitActiveSec,
                               OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Spawning new server with waiting for ACTIVE state for {}. Name={}, flavor={}, image={}",
                waitActiveSec, name, flavor.getName(), image.getName());
        if (userData != null) {
            userData = Base64.getEncoder().encodeToString(userData.getBytes());
        }

        ServerCreate serverCreate = Builders.server()
                .name(name)
                .flavor(flavor.getId())
                .image(image.getId())
                .keypairName(keypairName)
                .addSecurityGroup(securityGroup)
                .userData(userData)
                .build();

        Server server = client.compute().servers().bootAndWaitActive(serverCreate, waitActiveSec * 1000);
        if (server == null) {
            throw new OpenstackException(CREATE_SERVER_FAILED, "Failed to create server '%s'", name);
        }

        return server;
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
                log.debug("Connection with openstack lost during serverConditionalWait on server {}. ",
                        server.getName(), e);
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

    @NotNull
    private List<OpenstackException> getResultsExceptions(List<?> results) {
        return results.stream()
                .filter(result -> OpenstackException.class.isAssignableFrom(result.getClass()))
                .map(OpenstackException.class::cast)
                .toList();
    }

    private void deleteServer(Server server, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Deleting server '{}'", server.getName());

        ActionResponse response = client.compute().servers().delete(server.getId());
        if (!response.isSuccess()) {
            throw new OpenstackException(DELETE_SERVER_FAILED,
                    "Failed to delete server %s. Reason: %s", server.getName(), response.getFault());
        }
    }

    private List<String> parallelDeleteServers(List<Server> servers, Supplier<OSClient.OSClientV3> clientSupplier)
            throws OpenstackException {
        List<?> results = servers.stream().parallel()
                .map(s -> {
                    try {
                        deleteServer(s, clientSupplier.get());
                        return s;
                    } catch (OpenstackException e) {
                        return e;
                    }
                }).toList();

        List<OpenstackException> exceptions = getResultsExceptions(results);
        if (!exceptions.isEmpty()) {
            throw new OpenstackException(DELETE_SERVER_FAILED,
                    "Failed to delete %d servers from '%s'. Exceptions: %s",
                    results.size(), servers.stream().map(Server::getName).toString(), exceptions.toString());
        }

        return servers.stream()
                .map(Server::getName)
                .toList();
    }
}
