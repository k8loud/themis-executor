package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.*;

@Service
@Slf4j
public class OpenstackGlanceServiceImpl implements OpenstackGlanceService {
    @Override
    public Image deleteTheOldestSnapshot(Server server, boolean keepOneSnapshot,
                                        OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Deleting the oldest snapshot from server {}", server.getName());
        Image imageToDelete = getTheOldestSnapshot(server, keepOneSnapshot, client);
        ActionResponse response = client.imagesV2().delete(imageToDelete.getId());
        if (!response.isSuccess()) {
            throw new OpenstackException(DELETE_SERVER_SNAPSHOT_FAILED,
                    "Failed to delete server %s snapshot %s. Reason: %s",
                    server.getName(), imageToDelete.getName(), response.getFault());
        }

        return imageToDelete;
    }

    @Override
    public Image getImage(String imageId, OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Getting Image object from imageID {}", imageId);
        Image image = client.imagesV2().get(imageId);
        if (image == null) {
            throw new OpenstackException(IMAGE_NOT_EXISTS, "Failed to find image with id '%s'", imageId);
        }
        return image;
    }

    private Image getTheOldestSnapshot(Server server, boolean keepOneSnapshot,
                                       OSClient.OSClientV3 client) throws OpenstackException {
        List<Image> a = client.imagesV2().list().stream()
                .filter(i -> server.getId().equals(i.getInstanceUuid()))
                .filter(i -> !i.getIsProtected())
                .map(i -> (Image) i)
                .sorted(Comparator.comparing(Image::getUpdatedAt))
                .toList();

        if (a.isEmpty()) {
            throw new OpenstackException(DELETE_SERVER_SNAPSHOT_FAILED, "Server %s does not have any snapshots",
                    server.getName());
        } else if (a.size() == 1 && keepOneSnapshot) {
            throw new OpenstackException(DELETE_SERVER_SNAPSHOT_FAILED,
                    "Server %s has 1 snapshots, and keepOneSnapshot was set on true", server.getName());
        }

        return a.get(0);
    }
}
