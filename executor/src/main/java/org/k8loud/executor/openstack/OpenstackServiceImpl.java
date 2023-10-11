package org.k8loud.executor.openstack;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient.OSClientV3;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.springframework.stereotype.Service;

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
    public void resizeServer(String region, String serverId, String newFlavorId) throws OpenstackException {
        OSClientV3 client = openstackClientWithRegion(region);
        Server server = openstackNovaService.getServer(serverId, client);
        Flavor newFlavor = openstackNovaService.getFlavor(newFlavorId, client);
        openstackNovaService.resize(server, newFlavor, client);
        openstackNovaService.waitForServerStatus(server, Server.Status.VERIFY_RESIZE, 120, client);
        openstackNovaService.confirmResize(server, client);
        log.info("Resizing a server with id={} finished with success", serverId);
    }

    @NotNull
    private OSClientV3 openstackClientWithRegion(String region) throws OpenstackException {
        OSClientV3 client = openstackClientProvider.getClientFromToken();
        client.useRegion(region);
        return client;
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
}
