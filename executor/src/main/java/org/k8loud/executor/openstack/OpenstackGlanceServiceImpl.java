package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.DELETE_SERVER_SNAPSHOT_FAILED;

@Service
@Slf4j
public class OpenstackGlanceServiceImpl implements OpenstackGlanceService {
    @Override
    public void deleteTheOldestSnapshot(Server server, boolean keepOneSnapshot,
                                        OSClient.OSClientV3 client) throws OpenstackException {
        log.debug("Deleting the oldest snapshot from server {}", server.getName());
        Image imageToDelete = getTheOldestSnapshot(server, keepOneSnapshot, client);
        ActionResponse response = client.imagesV2().delete(imageToDelete.getId());
        if (!response.isSuccess()) {
            log.error("Failed to delete server (name={}) snapshot (name={}). Reason: {}",
                    server.getName(), imageToDelete.getName(), response.getFault());
            throw new OpenstackException(response.getFault(), DELETE_SERVER_SNAPSHOT_FAILED);
        }
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
            log.error("Server {} does not have any snapshots", server.getName());
            throw new OpenstackException(DELETE_SERVER_SNAPSHOT_FAILED, "Server %s does not have any snapshots",
                    server.getName());
        } else if (a.size() == 1 && keepOneSnapshot) {
            log.error("Server {} has 1 snapshots, and keepOneSnapshot was set on true", server.getName());
            throw new OpenstackException(DELETE_SERVER_SNAPSHOT_FAILED,
                    "Server %s has 1 snapshots, and keepOneSnapshot was set on true", server.getName());
        }

        return a.get(0);
    }
}
