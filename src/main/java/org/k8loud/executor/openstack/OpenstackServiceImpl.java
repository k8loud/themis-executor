package org.k8loud.executor.openstack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.network.SecurityGroup;
import org.openstack4j.model.network.SecurityGroupRule;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;
import static org.k8loud.executor.util.Util.resultMap;
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
    private final TaskScheduler taskScheduler;


    //TODO resize with looking for bigger/smaller available flavor
    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "RESIZE_SERVER_FAILED")
    public Map<String, Object> resizeServerUp(String region, String serverId,
                                              String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavors(newFlavor, server.getFlavor());

        return resizeServer(server, newFlavor, 120, client);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "RESIZE_SERVER_FAILED")
    public Map<String, Object> resizeServerDown(String region, String serverId,
                                                String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavorsDisksSizesEquals(server, newFlavor);
        validateFlavors(server.getFlavor(), newFlavor);

        return resizeServer(server, newFlavor, 120, client);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "GET_SERVER_NAMES_FAILED")
    public Map<String, Object> getServerNames(String region, String namePattern)
            throws OpenstackException, ValidationException {
        log.info("Getting servers in region '{}' with namePattern '{}'", region, namePattern);
        OSClientV3 client = openstackClientWithRegion(region);

        List<String> serverNames = openstackNovaService.getServers(client, Pattern.compile(namePattern)).stream()
                .map(Server::getName)
                .toList();

        String result = String.format("Getting servers in region '%s' with namePattern '%s' finished with success",
                region, namePattern);
        return resultMap(result, Map.of("serverNames", serverNames));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SERVER_FAILED")
    public Map<String, Object> createServers(String region, String name, String imageId, String flavorId,
                                             String keypairName, String securityGroup, String userData, int count,
                                             int waitActiveSec) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Image image = openstackGlanceService.getImage(imageId, client);
        Flavor flavor = openstackNovaService.getFlavor(flavorId, client);

        List<String> serverIds = openstackNovaService.createServers(name, image, flavor, keypairName, securityGroup,
                userData, count, waitActiveSec, clientSupplier(region));
        String result = String.format("Creating %d instances named '%s' finished with success", count, name);
        return resultMap(result, Map.of("serverIds", serverIds));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DELETE_SERVER_FAILED")
    public Map<String, Object> deleteServers(String region, String namePattern)
            throws OpenstackException, ValidationException {
        Pattern namePatternObj = Pattern.compile(namePattern);

        List<String> deletedServers = openstackNovaService.deleteServers(namePatternObj, clientSupplier(region));

        String result = String.format("Deleting instances named with pattern %s finished with success.",
                namePattern);
        return resultMap(result, Map.of("deletedServers", deletedServers));
    }

    @Override
    public Map<String, Object> deleteServers(String region, List<String> serverIds)
            throws OpenstackException, ValidationException {
        List<Server> servers = serverIds.stream()
                .parallel()
                .map(serverId -> {
                    try {
                        return openstackNovaService.getServer(serverId, clientSupplier(region).get());
                    } catch (OpenstackException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        List<String> deletedServers = openstackNovaService.deleteServers(servers, clientSupplier(region));

        String result = "Deleting instances from serverList finished with success";
        return resultMap(result, Map.of("deletedServers", deletedServers));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "ATTACH_VOLUME_FAILED")
    public Map<String, Object> attachVolume(String region, String serverId, String volumeId,
                                            String device) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Volume volume = openstackCinderService.getVolume(volumeId, client);

        //this works for volumes with multiattach=false. Our clouds version is not supporting multiattach volume type
        if (volume.getStatus() != Volume.Status.AVAILABLE) {
            throw new OpenstackException(
                    String.format("Volume %s has status %s, but available is needed", volume.getName(),
                            volume.getStatus()), VOLUME_ERROR);
        }

        openstackCinderService.attachVolume(server, volume, device, client);
        return resultMap(
                String.format("Attaching volume with id=%s for a server with id=%s to device=%s finished with success",
                        volumeId, serverId, device));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DETACH_VOLUME_FAILED")
    public Map<String, Object> detachVolume(String region, String serverId, String volumeId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Volume volume = openstackCinderService.getVolume(volumeId, client);

        if (volume.getStatus() != Volume.Status.IN_USE) {
            throw new OpenstackException(
                    String.format("Volume %s has status %s, but in_use is needed", volume.getName(),
                            volume.getStatus()), VOLUME_ERROR);
        }

        openstackCinderService.detachVolume(server, volume, client);
        return resultMap(String.format("Detached volume with id=%s from a server with id=%s finished with success",
                volumeId, serverId));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "PAUSE_SERVER_FAILED")
    public Map<String, Object> pauseServer(String region, String serverId) throws OpenstackException {
        return basicServerAction(region, serverId, PAUSE);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "UNPAUSE_SERVER_FAILED")
    public Map<String, Object> unpauseServer(String region, String serverId) throws OpenstackException {
        return basicServerAction(region, serverId, UNPAUSE);
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SERVER_SNAPSHOT_FAILED")
    public Map<String, Object> createServerSnapshot(String region, String serverId, String snapshotName,
                                                    boolean stopInstance) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        snapshotName = Optional.ofNullable(snapshotName).filter(s -> !s.isBlank()).orElse(generateSnapshotName(server));
        openstackNovaService.waitForServerStatus(server, ACTIVE, 60, client);

        String snapshotId = (stopInstance) ?
                createSnapshotOnStoppedServer(snapshotName, client, server) :
                createSnapshotAndWait(snapshotName, client, server);

        String result = String.format("Created snapshot with name %s on server %s", snapshotName, server.getName());
        return resultMap(result, Map.of("snapshotId", snapshotId));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DELETE_SERVER_SNAPSHOT_FAILED")
    public Map<String, Object> deleteTheOldestServerSnapshot(String region, String serverId,
                                                             boolean keepOneSnapshot) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Image deletedImage = openstackGlanceService.deleteTheOldestSnapshot(server, keepOneSnapshot, client);
        return resultMap(String.format("Deleted the oldest snapshot from server %s", server.getName()),
                Map.of("deletedSnapshot", deletedImage.getName()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_VOLUME_SNAPSHOT_FAILED")
    public Map<String, Object> createVolumeSnapshot(String region, String volumeId,
                                                    String snapshotName) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Volume volume = openstackCinderService.getVolume(volumeId, client);
        snapshotName = Optional.ofNullable(snapshotName).filter(s -> !s.isBlank()).orElse(generateSnapshotName(volume));
        VolumeSnapshot snapshot = openstackCinderService.createVolumeSnapshot(volume, snapshotName, client);
        return resultMap(String.format("Created snapshot with name %s for volume %s", snapshotName, volume.getName()),
                Map.of("snapshotId", snapshot.getId()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "DELETE_VOLUME_SNAPSHOT_FAILED")
    public Map<String, Object> deleteTheOldestVolumeSnapshot(String region, String volumeId,
                                                             boolean keepOneSnapshot) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Volume volume = openstackCinderService.getVolume(volumeId, client);
        VolumeSnapshot snapshot = openstackCinderService.deleteTheOldestVolumeSnapshot(volume, keepOneSnapshot, client);
        return resultMap(String.format("Deleted the oldest snapshot from volume %s", volume.getName()),
                Map.of("snapshotId", snapshot.getId()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "CREATE_SECURITY_GROUP_FAILED")
    public Map<String, Object> createSecurityGroup(String region, String name,
                                                   String description) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        SecurityGroup securityGroup = openstackNeutronService.createSecurityGroup(name, description, client);

        String result = String.format("Created security group named %s with description %s", name, description);
        return resultMap(result, Map.of("securityGroupId", securityGroup.getId()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "REMOVE_SECURITY_GROUP_FAILED")
    public Map<String, Object> removeSecurityGroup(String region, String securityGroupId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        SecurityGroup securityGroup = openstackNeutronService.getSecurityGroup(securityGroupId, client);
        openstackNeutronService.removeSecurityGroup(securityGroup, client);

        return resultMap(String.format("Deleted security group named %s", securityGroup.getName()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "ADD_SECURITY_GROUP_FAILED")
    public Map<String, Object> addSecurityGroupToInstance(String region, String securityGroupId,
                                                          String serverId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        SecurityGroup securityGroup = openstackNeutronService.getSecurityGroup(securityGroupId, client);
        openstackNovaService.addSecurityGroupToInstance(server, securityGroup, client);
        return resultMap(String.format("Added SecurityGroup named %s to Server named %s", securityGroup.getName(),
                server.getName()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "REMOVE_SECURITY_GROUP_FAILED")
    public Map<String, Object> removeSecurityGroupFromInstance(String region, String securityGroupId,
                                                               String serverId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        SecurityGroup securityGroup = openstackNeutronService.getSecurityGroup(securityGroupId, client);
        openstackNovaService.removeSecurityGroupFromInstance(server, securityGroup, client);
        return resultMap(String.format("Removed SecurityGroup named %s to Server named %s", securityGroup.getName(),
                server.getName()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "ADD_RULE_FAILED")
    public Map<String, Object> addRuleToSecurityGroup(String region, String securityGroupId, String ethertype,
                                                      String direction, String remoteIpPrefix, String protocol,
                                                      int portRangeMin, int portRangeMax,
                                                      String description) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        SecurityGroup securityGroup = openstackNeutronService.getSecurityGroup(securityGroupId, client);
        SecurityGroupRule securityGroupRule = openstackNeutronService.addSecurityGroupRule(securityGroup, ethertype,
                direction, remoteIpPrefix, protocol, portRangeMin, portRangeMax, description, client);

        String result = String.format("Added new rule (%s) to securityGroup named %s", securityGroupRule.toString(),
                securityGroup.getName());
        return resultMap(result, Map.of("ruleId", securityGroupRule.getId()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "REMOVE_RULE_FAILED")
    public Map<String, Object> removeSecurityGroupRule(String region,
                                                       String securityGroupRuleId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        SecurityGroupRule securityGroupRule = openstackNeutronService.getSecurityGroupRule(securityGroupRuleId, client);
        openstackNeutronService.removeSecurityGroupRule(securityGroupRule, client);
        return resultMap(String.format("Deleted securityGroupRule (%s)", securityGroupRule.toString()));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "IP_THROTTLE_FAILED")
    public Map<String, Object> throttle(String region, String serverId, String ethertype,
                                        String remoteIpPrefix, String protocol, int portRangeMin, int portRangeMax,
                                        long secDuration) throws OpenstackException, ValidationException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Map<SecurityGroup, Set<SecurityGroupRule>> rulesToModify = openstackNeutronService.getThrottlingRules(server,
                ethertype, remoteIpPrefix, protocol, portRangeMin, portRangeMax, client);

        Set<SecurityGroupRule> throttlingRules = openstackNeutronService.modifySecurityGroupsDuringThrottling(
                rulesToModify, remoteIpPrefix, portRangeMin, portRangeMax, clientSupplier(region));

        scheduleIpThrottlingRemoval(region, throttlingRules, rulesToModify, secDuration);
        return resultMap(
                String.format("Added throttling security group rules to server %s and scheduled revert in %d sec",
                        server.getName(), secDuration),
                Map.of("affectedRules", rulesToModify, "createdRules", throttlingRules));
    }

    private void scheduleIpThrottlingRemoval(String region, Set<SecurityGroupRule> rulesToDelete,
                                             Map<SecurityGroup, Set<SecurityGroupRule>> rulesToRestore,
                                             long secDuration) {
        log.info("Scheduling IP throttling removal to start in {} seconds", secDuration);
        taskScheduler.schedule(() -> {
                    try {
                        removeThrottling(region, rulesToDelete, rulesToRestore);
                    } catch (OpenstackException e) {
                        log.warn("Problem during removing throttling. ", e);
                    }
                },
                Instant.now().plus(secDuration, ChronoUnit.SECONDS));
    }

    @NotNull
    private OSClientV3 openstackClientWithRegion(String region) throws OpenstackException {
        OSClientV3 client = openstackClientProvider.getClientFromToken();
        client.useRegion(region);
        return client;
    }

    private void removeThrottling(String region, Set<SecurityGroupRule> rulesToDelete,
                                  Map<SecurityGroup, Set<SecurityGroupRule>> rulesToRestore) throws OpenstackException {
        log.info("Removing throttling security group rules and restoring previous ones");

        rulesToRestore.keySet().stream().parallel().forEach(group -> {
            rulesToRestore.get(group).stream().parallel().forEach(rule -> {
                try {
                    openstackNeutronService.addSecurityGroupRule(group, rule.getEtherType(), rule.getDirection(),
                            rule.getRemoteIpPrefix(), rule.getProtocol(), rule.getPortRangeMin(),
                            rule.getPortRangeMax(), rule.getDescription(), openstackClientWithRegion(region));
                } catch (OpenstackException e) {
                    throw new RuntimeException(e);
                }
            });
        });

        rulesToDelete.stream().parallel()
                .forEach(rules -> openstackNeutronService.removeSecurityGroupRule(rules, clientSupplier(region).get()));
    }

    private Map<String, Object> resizeServer(Server server, Flavor newFlavor, int waitSecondsForVerifyStatus,
                                             OSClientV3 client) throws OpenstackException {
        openstackNovaService.resize(server, newFlavor, client);
        openstackNovaService.waitForServerStatus(server, Server.Status.VERIFY_RESIZE, waitSecondsForVerifyStatus,
                client);
        openstackNovaService.confirmResize(server, client);
        return resultMap(String.format("Resizing a server with id=%s finished with success", server.getId()));
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
                    String.format("Flavor with id=%s is not bigger than Flavor with id=%s", biggerFlavor.getId(),
                            smallerFlavor.getId()), FLAVORS_COMPARISON);
        }
    }

    private Map<String, Object> basicServerAction(String region, String serverId,
                                                  Action action) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        openstackNovaService.basicServerAction(server, action, client);
        return resultMap(String.format("Performing action %s on server with id=%s finished with success", action.name(),
                serverId));
    }

    private String createSnapshotOnStoppedServer(String snapshotName, OSClientV3 client,
                                                 Server server) throws OpenstackException {
        openstackNovaService.basicServerAction(server, STOP, client);
        openstackNovaService.waitForServerStatus(server, SHUTOFF, 60, client);

        String snapshotId = createSnapshotAndWait(snapshotName, client, server);

        openstackNovaService.basicServerAction(server, START, client);
        openstackNovaService.waitForServerStatus(server, ACTIVE, 60, client);

        return snapshotId;
    }

    private String createSnapshotAndWait(String snapshotName, OSClientV3 client,
                                         Server server) throws OpenstackException {
        String snapshotId = openstackNovaService.createServerSnapshot(server, snapshotName, client);
        openstackNovaService.waitForServerTaskEnd(server, 600, client);
        return snapshotId;
    }

    @NotNull
    private String generateSnapshotName(Server server) {
        return server.getName() + "-snapshot-" + Instant.now();
    }

    @NotNull
    private String generateSnapshotName(Volume volume) {
        return volume.getName() + "-volume-" + Instant.now();
    }

    private Supplier<OSClientV3> clientSupplier(String region) {
        return () -> {
            try {
                return openstackClientWithRegion(region);
            } catch (OpenstackException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
