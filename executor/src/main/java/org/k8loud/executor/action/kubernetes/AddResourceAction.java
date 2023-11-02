package org.k8loud.executor.action.kubernetes;

import data.Params;
import exception.ParamNotFoundException;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.exception.KubernetesException;
import org.k8loud.executor.kubernetes.KubernetesService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddResourceAction extends KubernetesAction {
    private static final String DUMMY_RESOURCE_DESCRIPTION = """
            apiVersion: v1
            kind: Pod
            metadata:
              name: nginx
            spec:
              containers:
              - name: nginx
                image: nginx:1.14.2
                ports:
                - containerPort: 80
            """;
    private String namespace;
    private String resourceType;
    private String resourceDescription;

    public AddResourceAction(Params params, KubernetesService kubernetesService) throws ActionException {
        super(params, kubernetesService);
    }

    @Override
    public void unpackParams(Params params) {
        namespace = params.getRequiredParam("namespace"); // May be optional since resourceDescription can hold that information
        resourceDescription = params.getOptionalParam("resourceDescription", DUMMY_RESOURCE_DESCRIPTION);
        resourceType = extractResourceType(resourceDescription);
    }

    @Override
    public String performKubernetesAction() throws KubernetesException {
        return kubernetesService.addResource(namespace, resourceType, resourceDescription);
    }

    private String extractResourceType(String resourceDescription) {
        Pattern pattern = Pattern.compile("kind: (\\w+)");
        Matcher matcher = pattern.matcher(resourceDescription);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new ParamNotFoundException("kind in resourceDescription hasn't been found");
    }
}
