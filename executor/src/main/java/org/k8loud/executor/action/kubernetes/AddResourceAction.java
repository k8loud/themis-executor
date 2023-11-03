package org.k8loud.executor.action.kubernetes;

import data.Params;
import exception.ParamNotFoundException;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddResourceAction extends KubernetesAction {
    private String resourceType;
    private String resourceDescription;

    public AddResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackParams(Params params) {
        resourceDescription = params.getRequiredParam("resourceDescription");
        namespace = params.getOptionalParam("namespace", null);
        if (namespace == null) {
            namespace = extractNamespace(resourceDescription).orElseThrow(() ->
                    new ParamNotFoundException(
                            "namespace has been neither passed as a param nor as a part of resourceDescription"));
        }
        resourceType = extractResourceType(resourceDescription);
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        // empty
    }

    @Override
    public String executeBody() throws KubernetesException {
        return kubernetesService.addResource(namespace, resourceType, resourceDescription);
    }

    // TODO: Add this check to namespace, resourceName for all Kube actions
    private Optional<String> extractNamespace(String resourceDescription) {
        Pattern pattern = Pattern.compile("namespace: (\\b[a-z0-9][a-z0-9-]*[a-z0-9]\\b)");
        Matcher matcher = pattern.matcher(resourceDescription);
        if (matcher.find() && matcher.groupCount() > 0) {
            String namespace = matcher.group(1);
            if (namespace.length() < 64) {
                return Optional.of(namespace);
            }
        }
        return Optional.empty();
    }

    private String extractResourceType(String resourceDescription) {
        Pattern pattern = Pattern.compile("kind: (\\w+)");
        Matcher matcher = pattern.matcher(resourceDescription);
        if (matcher.find() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        throw new ParamNotFoundException("kind in resourceDescription hasn't been found");
    }
}
