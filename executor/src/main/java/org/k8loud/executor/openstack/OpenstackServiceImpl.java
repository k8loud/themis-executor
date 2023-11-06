package org.k8loud.executor.openstack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;
import static org.openstack4j.model.compute.Action.*;
import static org.openstack4j.model.compute.Server.Status.ACTIVE;
import static org.openstack4j.model.compute.Server.Status.SHUTOFF;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenstackServiceImpl implements OpenstackService {
    private final OpenstackClientProvider openstackClientProvider;
    private final OpenstackNovaService openstackNovaService;
    private final OpenstackCinderService openstackCinderService;
    private final OpenstackGlanceService openstackGlanceService;
    private final OpenstackNeutronService openstackNeutronService;

    //TODO resize with looking for bigger/smaller available flavor
    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "RESIZE_SERVER_FAILED")
    public String resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavors(newFlavor, server.getFlavor());

        return resizeServer(server, newFlavor, 120, client);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "RESIZE_SERVER_FAILED")
    public String resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavorsDisksSizesEquals(server, newFlavor);
        validateFlavors(server.getFlavor(), newFlavor);

        return resizeServer(server, newFlavor, 120, client);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SERVER_FAILED")
    public String createServers(String region, String name, String imageId, String flavorId, String keypairName,
                                String securityGroup, String userData, int count,
                                int waitActiveSec) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Image image = openstackGlanceService.getImage(imageId, client);
        Flavor flavor = openstackNovaService.getFlavor(flavorId, client);

        openstackNovaService.createServers(name, image, flavor, keypairName, securityGroup, userData, count,
                waitActiveSec, () -> {
                    try {
                        return openstackClientWithRegion(region);
                    } catch (OpenstackException e) {
                        throw new RuntimeException(e);
                    }
                });
        return String.format("Creating %s instances named %s finished with success", count, name);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "ATTACH_VOLUME_FAILED")
    public String attachVolume(String region, String serverId, String volumeId,
                               String device) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Volume volume = openstackCinderService.getVolume(volumeId, client);

        //this works for volumes with multiattach=false. Our clouds version is not supporting multiattach volume type
        if (volume.getStatus() != Volume.Status.AVAILABLE) {
            throw new OpenstackException(String.format("Volume %s has status %s, but available is needed",
                    volume.getName(), volume.getStatus()), VOLUME_ERROR);
        }

        openstackCinderService.attachVolume(server, volume, device, client);
        return String.format("Attaching volume with id=%s for a server with id=%s to device=%s finished with success",
                volumeId, serverId, device);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DETACH_VOLUME_FAILED")
    public String detachVolume(String region, String serverId, String volumeId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Volume volume = openstackCinderService.getVolume(volumeId, client);

        if (volume.getStatus() != Volume.Status.IN_USE) {
            throw new OpenstackException(String.format("Volume %s has status %s, but in_use is needed",
                    volume.getName(), volume.getStatus()), VOLUME_ERROR);
        }

        openstackCinderService.detachVolume(server, volume, client);
        return String.format("Detached volume with id=%s from a server with id=%s finished with success",
                volumeId, serverId);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "PAUSE_SERVER_FAILED")
    public String pauseServer(String region, String serverId) throws OpenstackException {
        return basicServerAction(region, serverId, PAUSE);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "UNPAUSE_SERVER_FAILED")
    public String unpauseServer(String region, String serverId) throws OpenstackException {
        return basicServerAction(region, serverId, UNPAUSE);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SERVER_SNAPSHOT_FAILED")
    public String createServerSnapshot(String region, String serverId, String snapshotName,
                                       boolean stopInstance) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        snapshotName = Optional.ofNullable(snapshotName).filter(s -> !s.isBlank()).orElse(generateSnapshotName(server));
        openstackNovaService.waitForServerStatus(server, ACTIVE, 60, client);
        if (stopInstance) {
            createSnapshotOnStoppedServer(snapshotName, client, server);
        } else {
            createSnapshotAndWait(snapshotName, client, server);
        }
        return String.format("Created snapshot with name %s on server %s", snapshotName, server.getName());
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DELETE_SERVER_SNAPSHOT_FAILED")
    public String deleteTheOldestServerSnapshot(String region, String serverId,
                                                boolean keepOneSnapshot) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        openstackGlanceService.deleteTheOldestSnapshot(server, keepOneSnapshot, client);
        return String.format("Deleted the oldest snapshot from server %s", server.getName());
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_VOLUME_SNAPSHOT_FAILED")
    public String createVolumeSnapshot(String region, String volumeId, String snapshotName) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Volume volume = openstackCinderService.getVolume(volumeId, client);
        snapshotName = Optional.ofNullable(snapshotName).filter(s -> !s.isBlank()).orElse(generateSnapshotName(volume));
        openstackCinderService.createVolumeSnapshot(volume, snapshotName, client);
        return String.format("Created snapshot with name %s for volume %s", snapshotName, volume.getName());
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DELETE_VOLUME_SNAPSHOT_FAILED")
    public String deleteTheOldestVolumeSnapshot(String region, String volumeId,
                                                boolean keepOneSnapshot) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Volume volume = openstackCinderService.getVolume(volumeId, client);
        openstackCinderService.deleteTheOldestVolumeSnapshot(volume, keepOneSnapshot, client);
        return String.format("Deleted the oldest snapshot from volume %s", volume.getName());
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SECURITY_GROUP_FAILED")
    public String createSecurityGroup(String region, String name, String description) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        openstackNeutronService.createSecurityGroup(name, description, client);
        return String.format("Created security group named %s with description %s", name, description);
    }

    @NotNull
    private OSClientV3 openstackClientWithRegion(String region) throws OpenstackException {
        OSClientV3 client = openstackClientProvider.getClientFromToken();
        client.useRegion(region);
        return client;
    }

    private String resizeServer(Server server, Flavor newFlavor, int waitSecondsForVerifyStatus,
                                OSClientV3 client) throws OpenstackException {
        openstackNovaService.resize(server, newFlavor, client);
        openstackNovaService.waitForServerStatus(server, Server.Status.VERIFY_RESIZE, waitSecondsForVerifyStatus,
                client);
        openstackNovaService.confirmResize(server, client);
        return String.format("Resizing a server with id=%s finished with success", server.getId());
    }

    private void validateFlavorsDisksSizesEquals(Server server, Flavor newFlavor) throws OpenstackException {
        if (server.getFlavor().getDisk() != newFlavor.getDisk()) {
            throw new OpenstackException(String.format(
                    "Cannot resize server with id=%s. Given flavor disk size (%d) is not equals to current one (%d)",
                    server.getId(), server.getFlavor().getDisk(), newFlavor.getDisk()), FLAVORS_DISKS_NOT_SAME);
        }
    }

    private void validateFlavors(Flavor biggerFlavor, Flavor smallerFlavor) throws OpenstackException {
        int comparisonResult = Comparator.comparingInt(Flavor::getVcpus)
                .thenComparingInt(Flavor::getRam)
                .thenComparingInt(Flavor::getDisk)
                .compare(biggerFlavor, smallerFlavor);

        if (comparisonResult <= 0) {
            throw new OpenstackException(
                    String.format("Flavor with id=%s is not bigger than Flavor with id=%s",
                            biggerFlavor.getId(), smallerFlavor.getId()), FLAVORS_COMPARISON);
        }
    }

    private String basicServerAction(String region, String serverId, Action action) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        openstackNovaService.basicServerAction(server, action, client);
        return String.format("Performing action %s on server with id=%s finished with success", action.name(),
                serverId);
    }

    private void createSnapshotOnStoppedServer(String snapshotName, OSClientV3 client,
                                               Server server) throws OpenstackException {
        openstackNovaService.basicServerAction(server, STOP, client);
        openstackNovaService.waitForServerStatus(server, SHUTOFF, 60, client);

        createSnapshotAndWait(snapshotName, client, server);

        openstackNovaService.basicServerAction(server, START, client);
        openstackNovaService.waitForServerStatus(server, ACTIVE, 60, client);
    }

    private void createSnapshotAndWait(String snapshotName, OSClientV3 client,
                                       Server server) throws OpenstackException {
        openstackNovaService.createServerSnapshot(server, snapshotName, client);
        openstackNovaService.waitForServerTaskEnd(server, 600, client);
    }

    @NotNull
    private String generateSnapshotName(Server server) {
        return server.getName() + "-snapshot-" + Instant.now();
    }

    @NotNull
    private String generateSnapshotName(Volume volume) {
        return volume.getName() + "-volume-" + Instant.now();
    }
}
