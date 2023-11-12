package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.Flavor;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.network.SecurityGroup;
import org.springframework.retry.annotation.Retryable;

import java.util.function.Supplier;

@Retryable(retryFor = ConnectionException.class)
public interface OpenstackNovaService {
    void resize(Server server, Flavor newFlavor, OSClient.OSClientV3 client) throws OpenstackException;

    void confirmResize(Server server, OSClient.OSClientV3 client) throws OpenstackException;

    String createServers(String name, Image image, Flavor flavor, String keypairName, String securityGroup,
                         String userData, int count, int waitActiveSec,
                         Supplier<OSClient.OSClientV3> clientSupplier) throws OpenstackException;

    Server getServer(String serverId, OSClient.OSClientV3 client) throws OpenstackException;

    Flavor getFlavor(String flavourId, OSClient.OSClientV3 client) throws OpenstackException;

    void basicServerAction(Server server, Action action, OSClient.OSClientV3 client) throws OpenstackException;

    void createServerSnapshot(Server server, String snapshotName, OSClient.OSClientV3 client) throws OpenstackException;

    void addSecurityGroupToInstance(Server server, SecurityGroup securityGroup, OSClient.OSClientV3 client) throws OpenstackException;

    void removeSecurityGroupFromInstance(Server server, SecurityGroup securityGroup, OSClient.OSClientV3 client) throws OpenstackException;

    @Retryable(maxAttempts = 1)
    void waitForServerStatus(Server server, Server.Status status, int waitingTimeSec, OSClient.OSClientV3 client);

    @Retryable(maxAttempts = 1)
    void waitForServerTaskEnd(Server server, int waitingTimeSec, OSClient.OSClientV3 client);
}
