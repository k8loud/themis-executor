package org.k8loud.executor.kubernetes;

import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.AppsAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.internal.HasMetadataOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.datastorage.DataStorageService;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.exception.ValidationException;
import org.k8loud.executor.util.Util;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.k8loud.executor.exception.code.KubernetesExceptionCode.*;
import static org.k8loud.executor.kubernetes.KubernetesResourceType.*;
import static org.k8loud.executor.util.Util.getFullResourceName;
import static org.k8loud.executor.util.Util.resultMap;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@Service
@Slf4j
public class KubernetesServiceImpl implements KubernetesService {
    private final KubernetesClientProvider clientProvider;
    private final DataStorageService dataStorageService;

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "SCALE_HORIZONTALLY_FAILED")
    public Map<String, Object> scaleHorizontally(String namespace, String resourceName, String resourceType,
                                                 Integer replicas) throws KubernetesException, ValidationException {
        log.info("Scaling {} to {}", getFullResourceName(resourceType, resourceName), replicas);
        int currentReplicas = 0;
        if ("Deploymnet".equals(resourceType)) {
            currentReplicas = getDeploymentReplicasNumber(namespace, resourceName);
        }
        getResource(namespace, resourceType, resourceName)
                .scale(replicas + currentReplicas > 0 ? replicas + currentReplicas: 1);

        return resultMap(String.format("Scaled %s to %d", getFullResourceName(resourceType, resourceName), replicas),
                Map.of("fullResourceName", getFullResourceName(resourceType, resourceName), "replicas", replicas));
    }

    private int getDeploymentReplicasNumber(String namespace, String resourceName) {
        return clientProvider.getKubernetesClient()
                .apps()
                .deployments()
                .inNamespace(namespace)
                .withName(resourceName)
                .get()
                .getSpec()
                .getReplicas();
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "ADD_RESOURCE_FAILED")
    public <T extends HasMetadata> Map<String, Object> addResource(final String namespace, final String resourceType,
                                                                   String resourceDescription) throws KubernetesException, ValidationException {
        log.info("Adding resource of type {} to namespace {} from description", resourceType, namespace);
        Resource<T> resource = loadResource(resourceType, resourceDescription);
        final String resourceName = ((HasMetadataOperation<T, ?, ?>) resource).getName();
        try {
            // This check may be removed, fabric8 responds with a bit lengthy message, but it's clear
            getResource(namespace, resourceType, resourceName);
            throw new KubernetesException(String.format("Resource %s already exists",
                    getFullResourceName(resourceType, resourceName)), RESOURCE_ALREADY_EXISTS);
        } catch (KubernetesException e) {
            if (e.getExceptionCode() == RESOURCE_NOT_FOUND) {
                addResource(namespace, resourceType, resource.item());
            } else if (e.getExceptionCode() == RESOURCE_ALREADY_EXISTS) {
                throw e;
            }
        }
        try {
            getResource(namespace, resourceType, resourceName);
        } catch (KubernetesException e) {
            throw new KubernetesException(String.format("Post add resource verification failed, the cause is '%s'", e),
                    POST_ADD_RESOURCE_VERIFICATION_FAILED);
        }
        return resultMap(String.format("Added resource %s", getFullResourceName(resourceType, resourceName)),
                Map.of("fullResourceName", getFullResourceName(resourceType, resourceName)));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "DELETE_RESOURCE_FAILED")
    public Map<String, Object> deleteResource(String namespace, String resourceName, String resourceType,
                                              Long gracePeriodSeconds)
            throws KubernetesException {
        log.info("Deleting {}, giving {} seconds for shutdown", getFullResourceName(resourceType, resourceName),
                gracePeriodSeconds);
        getResource(namespace, resourceType, resourceName)
                .withGracePeriod(gracePeriodSeconds)
                .delete();

        return resultMap(String.format("Deleted resource %s", getFullResourceName(resourceType, resourceName)));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException", exceptionCode = "UPDATE_CONFIG_MAP_FAILED")
    public Map<String, Object> updateConfigMap(String namespace, String resourceName, Map<String, String> replacements)
            throws KubernetesException, ValidationException {
        log.info("Updating {} with {}", getFullResourceName(CONFIG_MAP.toString(), resourceName), replacements);
        getResource(namespace, CONFIG_MAP.toString(), resourceName)
                .edit(c -> new ConfigMapBuilder((ConfigMap) c)
                        .addToData(replacements).build());
        return resultMap(
                String.format("Update of %s successful", getFullResourceName(CONFIG_MAP.toString(), resourceName)),
                Map.of("replacements", replacements));
    }

    // prepare a template of pod with updated resources -> delete pod -> recreate pod
    // Wanted to update resources for the container only, but they can't be changed in runtime
    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException",
            exceptionCode = "CHANGE_RESOURCES_OF_CONTAINER_WITHIN_POD_FAILED")
    public Map<String, Object> changeResourcesOfContainerWithinPodAction(String namespace, String podName,
                                                                         String containerName, String limitsCpu,
                                                                         String limitsMemory, String requestsCpu,
                                                                         String requestsMemory)
            throws KubernetesException {
        Resource<Pod> resource = getResource(namespace, POD.toString(), podName);
        final ResourceRequirements requirements = createResourceRequirements(limitsCpu, limitsMemory, requestsCpu,
                requestsMemory);
        final String fullResourceName = getFullResourceName(POD.toString(), podName);

        log.info("Looking for container {} within {}", containerName, fullResourceName);
        PodSpec spec = Optional.ofNullable(resource.get().getSpec()).orElseThrow(() -> new KubernetesException(
                String.format("Spec of %s is empty", fullResourceName), EMPTY_SPEC));
        List<Container> containers = spec.getContainers();
        Container container = findContainer(containers, containerName, fullResourceName);

        log.info("Preparing {} template", fullResourceName);
        Container updatedContainer = new ContainerBuilder(container).withResources(requirements).build();
        Pod updatedPod = new PodBuilder(resource.get())
                .editOrNewMetadata()
                    .withResourceVersion("")
                .endMetadata()
                .editOrNewSpec()
                    .removeFromContainers(container)
                    .addToContainers(updatedContainer)
                .endSpec()
                .build();

        final long gracePeriodSeconds = 1L;
        deleteResource(namespace, podName, POD.toString(), gracePeriodSeconds);
        try {
            Thread.sleep(gracePeriodSeconds * 1000);
        } catch (InterruptedException e) {
            log.warn("Interrupted while waiting for graceful resource deletion");
        }

        log.info("Recreating {}", fullResourceName);
        addResource(namespace, POD.toString(), updatedPod);

        return resultMap(String.format("Updated requirements specs of container %s within %s to {limitsCpu: %s, " +
                        "limitsMemory: %s, requestsCpu: %s, requestsMemory: %s}", containerName,
                fullResourceName, limitsCpu, limitsMemory, requestsCpu, requestsMemory));
    }

    @Override
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "KubernetesException",
            exceptionCode = "CHANGE_RESOURCES_OF_CONTAINER_WITHIN_DEPLOYMENT_FAILED")
    public Map<String, Object> changeResourcesOfContainerWithinDeploymentAction(String namespace, String deploymentName,
                                                                         String containerName, String limitsCpu,
                                                                         String limitsMemory, String requestsCpu,
                                                                         String requestsMemory)
            throws KubernetesException {
        Resource<Deployment> resource = getResource(namespace, DEPLOYMENT.toString(), deploymentName);
        final ResourceRequirements requirements = createResourceRequirements(limitsCpu, limitsMemory, requestsCpu,
                requestsMemory);
        final String fullResourceName = getFullResourceName(DEPLOYMENT.toString(), deploymentName);

        log.info("Looking for container {} within {}", containerName, fullResourceName);
        DeploymentSpec spec = Optional.ofNullable(resource.get().getSpec()).orElseThrow(() -> new KubernetesException(
                String.format("Spec of %s is empty", fullResourceName), EMPTY_SPEC));
        List<Container> containers = spec.getTemplate().getSpec().getContainers();
        // Verify that the container exists, not used later
        findContainer(containers, containerName, fullResourceName);

        clientProvider.getClient().apps().deployments()
                .inNamespace(namespace)
                .withName(deploymentName).edit(
                        d -> new DeploymentBuilder(d).editSpec().editTemplate()
                                    .editOrNewSpec()
                                        .editContainer(getContainerId(containers, containerName))
                                            .withResources(requirements)
                                        .endContainer()
                                    .endSpec()
                                .endTemplate().endSpec().build());

        return resultMap(String.format("Updated requirements specs of container %s within %s to {limitsCpu: %s, " +
                        "limitsMemory: %s, requestsCpu: %s, requestsMemory: %s}", containerName,
                fullResourceName, limitsCpu, limitsMemory, requestsCpu, requestsMemory));
    }

    @NotNull
    @Override
    public <T> Resource<T> getResource(String namespace, String resourceType, String resourceName)
            throws KubernetesException {
        log.debug("Looking for {} in {}", getFullResourceName(resourceType, resourceName), namespace);
        if (Util.emptyOrBlank(namespace)) {
            throw new KubernetesException(String.format("namespace '%s' is empty or blank", namespace),
                    EMPTY_OR_BLANK_NAMESPACE);
        } else if (Util.emptyOrBlank(resourceName)) {
            throw new KubernetesException(String.format("resourceName '%s' is empty or blank", resourceName),
                    EMPTY_OR_BLANK_RESOURCE_NAME);
        }
        MixedOperation<T, ?, ?> mixedOperation = getMixedOperation(resourceType);
        Resource<T> resource = mixedOperation.inNamespace(namespace).withName(resourceName);
        if (resource.get() == null) {
            throw new KubernetesException(String.format("Couldn't find '%s'",
                    getFullResourceName(resourceType, resourceName)), RESOURCE_NOT_FOUND);
        }
        return resource;
    }

    @Override
    public <T> Resource<T> loadResource(String resourceType, String resourceDescription)
            throws KubernetesException {
        log.info("Loading resource of type {} from description", resourceType);
        MixedOperation<T, ?, ?> mixedOperation = getMixedOperation(resourceType);
        String resourceDescriptionPath = dataStorageService.store(resourceType, resourceDescription);
        if (resourceDescriptionPath == null) {
            throw new KubernetesException(String.format("Resource file '%s' doesn't exist or isn't a file",
                    resourceDescriptionPath), RESOURCE_FILE_NOT_EXISTS);
        }
        Resource<T> resource = mixedOperation.load(resourceDescriptionPath);
        dataStorageService.remove(resourceDescriptionPath);
        return resource;
    }

    private <T> MixedOperation<T, ?, ?> getMixedOperation(String resourceType) throws KubernetesException {
        KubernetesResourceType resourceTypeObj = KubernetesResourceType.fromString(resourceType);
        KubernetesClient client = clientProvider.getClient();
        AppsAPIGroupDSL apps = client.apps();
        return (MixedOperation<T, ?, ?>) switch (resourceTypeObj) {
            case REPLICA_SET -> apps.replicaSets();
            case DEPLOYMENT -> apps.deployments();
            case STATEFUL_SET -> apps.statefulSets();
            case CONTROLLER_REVISION -> apps.controllerRevisions();
            case CONFIG_MAP -> client.configMaps();
            case POD -> client.pods();
        };
    }

    private <T> void addResource(String namespace, String resourceType, T resourceItem) throws KubernetesException {
        getMixedOperation(resourceType)
                .inNamespace(namespace)
                .resource(resourceItem)
                .create();
    }

    private ResourceRequirements createResourceRequirements(String limitsCpu, String limitsMemory, String requestsCpu,
                                                            String requestsMemory) {
        return new ResourceRequirementsBuilder()
                .addToLimits(Map.of("cpu", new Quantity(limitsCpu)))
                .addToLimits(Map.of("memory", new Quantity(limitsMemory)))
                .addToRequests(Map.of("cpu", new Quantity(requestsCpu)))
                .addToRequests(Map.of("memory", new Quantity(requestsMemory)))
                .build();
    }

    private Container findContainer(List<Container> containers, String containerName, String fullResourceName)
            throws KubernetesException {
        return containers.stream()
                .filter(c -> c.getName().equals(containerName))
                .findFirst()
                .orElseThrow(() -> new KubernetesException(String.format("Couldn't find container %s within %s",
                        containerName, fullResourceName), CONTAINER_NOT_FOUND));
    }

    private int getContainerId(List<Container> containers, String containerName) {
        return containers.stream().map(Container::getName).toList()
                .indexOf(containerName);
    }
}
