package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.VolumeAttachment;
import org.openstack4j.model.storage.block.VolumeSnapshot;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;

@Service
@Slf4j
public class OpenstackCinderServiceImpl implements OpenstackCinderService {
    @Override
    public void attachVolume(Server server, Volume volume, String device,
                             OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Attaching volume {} to a server {} with device={}", volume.getName(), server.getName(), device);
        ActionResponse response = client.blockStorage().volumes()
                .attach(volume.getId(), server.getId(), device, server.getHostId());

        if (!response.isSuccess()) {
            log.error("Failed to attach volume {} to server {}. Reason {}",
                    volume.getName(), server.getName(), response.getFault());
            throw new OpenstackException(response.getFault(), ATTACH_VOLUME_FAILED);
        }
    }

    @Override
    public void detachVolume(Server server, Volume volume, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Detaching volume {} from a server {}", volume.getName(), server.getName());
        String attachmentId = getAttachmentId(volume, server);
        ActionResponse response = client.blockStorage().volumes().detach(volume.getId(), attachmentId);

        if (!response.isSuccess()) {
            log.error("Failed to detach volume {} to server {}. Reason: {}",
                    volume.getName(), server.getName(), response.getFault());
            throw new OpenstackException(response.getFault(), DETACH_VOLUME_FAILED);
        }
    }

    @Override
    public Volume getVolume(String volumeId, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting volume object from volumeID {}", volumeId);
        Volume volume = client.blockStorage().volumes().get(volumeId);
        if (volume == null) {
            log.error("Failed to find volume with id={}", volumeId);
            throw new OpenstackException(VOLUME_NOT_EXITS);
        }
        return volume;
    }

    @Override
    public void createVolumeSnapshot(Volume volume, String snapshotName,
                                     OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Creating snapshot with name {} for volume {}", snapshotName, volume.getName());
        VolumeSnapshot snapshot = client.blockStorage().snapshots()
                .create(Builders.volumeSnapshot()
                        .name(snapshotName)
                        .volume(volume.getId())
                        .build()
                );

        if (snapshot == null) {
            log.error("Failed to create snapshot {} of a volume {}", snapshotName, volume.getName());
            throw new OpenstackException(CREATE_VOLUME_SNAPSHOT_FAILED,
                    "Failed to create snapshot %s of a volume %s", snapshotName, volume.getName());
        }
    }

    @Override
    public void deleteTheOldestVolumeSnapshot(Volume volume, boolean keepOneSnapshot,
                                              OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Deleting the oldest snapshot from volume {}", volume.getName());
        VolumeSnapshot snapshotToDelete = getTheOldestSnapshot(volume, keepOneSnapshot, client);
        ActionResponse response = client.blockStorage().snapshots().delete(snapshotToDelete.getId());
        if (!response.isSuccess()) {
            log.error("Failed to delete volume (name={}) snapshot (name={}). Reason: {}",
                    volume.getName(), snapshotToDelete.getName(), response.getFault());
            throw new OpenstackException(response.getFault(), DELETE_VOLUME_SNAPSHOT_FAILED);
        }
    }

    private VolumeSnapshot getTheOldestSnapshot(Volume volume, boolean keepOneSnapshot,
                                                OSClient.OSClientV3 client) throws OpenstackException {
        List<VolumeSnapshot> snapshots = client.blockStorage().snapshots().list().stream()
                .filter(v -> volume.getId().equals(v.getVolumeId()))
                .map(v -> (VolumeSnapshot) v)
                .sorted(Comparator.comparing(VolumeSnapshot::getCreated))
                .toList();

        if (snapshots.isEmpty()) {
            throw new OpenstackException(DELETE_VOLUME_SNAPSHOT_FAILED, "Volume %s does not have any snapshots",
                    volume.getName());
        } else if (snapshots.size() == 1 && keepOneSnapshot) {
            throw new OpenstackException(DELETE_VOLUME_SNAPSHOT_FAILED,
                    "Volume %s has 1 snapshot, and keepOneSnapshot was set on true", volume.getName());
        }

        return snapshots.get(0);
    }

    private String getAttachmentId(Volume volume, Server server) throws OpenstackException {
        List<VolumeAttachment> filteredAttachments = volume.getAttachments().stream()
                .filter(attachment -> Objects.equals(attachment.getServerId(), server.getId()))
                .map(v -> (VolumeAttachment) v)
                .toList();

        if (filteredAttachments.isEmpty()) {
            throw new OpenstackException(VOLUME_ERROR, "Volume %s has 0 attachments to server %s",
                    volume.getName(), server.getName());
        }

        return filteredAttachments.get(0).getAttachmentId();
    }
}
