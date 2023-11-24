package org.k8loud.executor.actions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.k8loud.executor.actions.command.CustomScriptAction;
import org.k8loud.executor.actions.kubernetes.DeleteResourceAction;
import org.k8loud.executor.actions.openstack.CreateServerSnapshotAction;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.openstack.OpenstackService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BuilderSanityTest {
    // Test at least one action from each collection

    @Mock
    CommandExecutionService commandExecutionService;
    @Mock
    KubernetesService kubernetesService;
    @Mock
    OpenstackService openstackService;

    @Test
    void testCommandActionBuilder() {
        // given
        final String HOST_KEY = "host";
        final String PORT_KEY = "port";
        final String COMMAND_KEY = "command";

        final String HOST = "127.0.0.1";
        final Integer PORT = 22;
        final String COMMAND = "ls -la";

        // when
        CustomScriptAction customScriptAction = CustomScriptAction.builder()
                .commandExecutionService(commandExecutionService)
                .host(HOST)
                .port(PORT)
                .command(COMMAND)
                .build();

        // then
        assertEquals(HOST, ReflectionTestUtils.getField(customScriptAction, HOST_KEY));
        assertEquals(PORT, ReflectionTestUtils.getField(customScriptAction, PORT_KEY));
        assertEquals(COMMAND, ReflectionTestUtils.getField(customScriptAction, COMMAND_KEY));
    }

    @Test
    void testKubernetesActionBuilder() {
        // given
        final String NAMESPACE_KEY = "namespace";
        final String RESOURCE_NAME_KEY = "resourceName";
        final String RESOURCE_TYPE_KEY = "resourceType";
        final String GRACE_PERIOD_SECONDS_KEY = "gracePeriodSeconds";

        final String NAMESPACE = "default";
        final String RESOURCE_NAME = "cm";
        final String RESOURCE_TYPE = "ConfigMap";
        final Long GRACE_PERIOD_SECONDS = 30L;

        // when
        DeleteResourceAction deleteResourceAction = DeleteResourceAction.builder()
                .kubernetesService(kubernetesService)
                .namespace(NAMESPACE)
                .resourceName(RESOURCE_NAME)
                .resourceType(RESOURCE_TYPE)
                .gracePeriodSeconds(GRACE_PERIOD_SECONDS)
                .build();

        // then
        assertEquals(NAMESPACE, ReflectionTestUtils.getField(deleteResourceAction, NAMESPACE_KEY));
        assertEquals(RESOURCE_NAME, ReflectionTestUtils.getField(deleteResourceAction, RESOURCE_NAME_KEY));
        assertEquals(RESOURCE_TYPE, ReflectionTestUtils.getField(deleteResourceAction, RESOURCE_TYPE_KEY));
        assertEquals(GRACE_PERIOD_SECONDS, ReflectionTestUtils.getField(deleteResourceAction, GRACE_PERIOD_SECONDS_KEY));
    }

    private String region;
    private String serverId;
    private String snapshotName;
    private boolean stopInstance;
    @Test
    void testOpenstackActionBuilder() {
        // given
        final String REGION_KEY = "region";
        final String SERVER_ID_KEY = "serverId";
        final String SNAPSHOT_NAME_KEY = "snapshotName";
        final String STOP_INSTANCE_KEY = "stopInstance";

        final String REGION = "region-123";
        final String SERVER_ID = "serverId-123";
        final String SNAPSHOT_NAME = "snapshot-123";
        final boolean STOP_INSTANCE = true;

        // when
        CreateServerSnapshotAction createServerSnapshotAction = CreateServerSnapshotAction.builder()
                .openstackService(openstackService)
                .region(REGION)
                .serverId(SERVER_ID)
                .snapshotName(SNAPSHOT_NAME)
                .stopInstance(STOP_INSTANCE)
                .build();

        // then
        assertEquals(REGION, ReflectionTestUtils.getField(createServerSnapshotAction, REGION_KEY));
        assertEquals(SERVER_ID, ReflectionTestUtils.getField(createServerSnapshotAction, SERVER_ID_KEY));
        assertEquals(SNAPSHOT_NAME, ReflectionTestUtils.getField(createServerSnapshotAction, SNAPSHOT_NAME_KEY));
        assertEquals(STOP_INSTANCE, ReflectionTestUtils.getField(createServerSnapshotAction, STOP_INSTANCE_KEY));
    }
}
