package org.k8loud.executor.openstack.cinder;

import org.junit.jupiter.api.BeforeEach;
import org.k8loud.executor.openstack.OpenstackCinderService;
import org.k8loud.executor.openstack.OpenstackCinderServiceImpl;
import org.k8loud.executor.openstack.OpenstackConstants;
import org.mockito.Mock;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.storage.BlockStorageService;
import org.openstack4j.api.storage.BlockVolumeService;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.storage.block.Volume;

public abstract class OpenstackCinderBaseTest extends OpenstackConstants {
    @Mock
    protected OSClient.OSClientV3 clientV3Mock;
    @Mock
    protected Server serverMock;
    @Mock
    protected Volume volumeMock;
    @Mock
    protected BlockStorageService blockStorageServiceMock;
    @Mock
    protected BlockVolumeService blockVolumeServiceMock;

    protected OpenstackCinderService openstackCinderService;

    @BeforeEach
    protected void baseSetUp() {
        openstackCinderService = new OpenstackCinderServiceImpl();
        setUp();
    }

    protected abstract void setUp();
}
