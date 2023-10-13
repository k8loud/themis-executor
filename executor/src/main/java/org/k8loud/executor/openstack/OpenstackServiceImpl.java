package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.FLAVORS_COMPARISON;
import static org.k8loud.executor.exception.code.OpenstackExceptionCode.FLAVORS_DISKS_NOT_SAME;

@Service
@Slf4j
public class OpenstackServiceImpl implements OpenstackService {
    private final OpenstackClientProvider openstackClientProvider;
    private final OpenstackNovaService openstackNovaService;

    public OpenstackServiceImpl(OpenstackClientProvider openstackClientProvider,
                                OpenstackNovaService openstackNovaService) {
        this.openstackClientProvider = openstackClientProvider;
        this.openstackNovaService = openstackNovaService;
    }

    //TODO resize with looking for bigger/smaller available flavor
    @Override
    public void resizeServerUp(String region, String serverId, String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavors(newFlavor, server.getFlavor());

        resizeServer(serverId, client, server, newFlavor, 120);
    }

    @Override
    public void resizeServerDown(String region, String serverId, String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);

        validateFlavorsDisksSizesEquals(server, newFlavor);
        validateFlavors(server.getFlavor(), newFlavor);

        resizeServer(serverId, client, server, newFlavor, 120);
    }

    private void resizeServer(String serverId, OSClientV3 client, Server server, Flavor newFlavor,
                              int waitSecondsForVerifyStatus) throws OpenstackException {
        openstackNovaService.resize(server, newFlavor, client);
        openstackNovaService.waitForServerStatus(server, Server.Status.VERIFY_RESIZE, waitSecondsForVerifyStatus,
                client);
        openstackNovaService.confirmResize(server, client);
        log.info("Resizing a server with id={} finished with success", serverId);
    }

    @Override
    public void copyServer(String region, String serverId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);

        openstackNovaService.createServer(server.getName() + "-copy", server.getFlavorId(), server.getImageId(), 30000,
                client);
        log.info("Copying a server with id={} finished with success", serverId);
        //FIXME right now we are only creating a new server with same flavor and image
    }

    @NotNull
    private OSClientV3 openstackClientWithRegion(String region) throws OpenstackException {
        OSClientV3 client = openstackClientProvider.getClientFromToken();
        client.useRegion(region);
        return client;
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
}
