package org.k8loud.executor.actions.command;

import lombok.Builder;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.exception.ActionException;
import org.k8loud.executor.model.Params;

public class DetachNodeFromKubernetesClusterAction extends CommandAction {
    private String nodeName;

    public DetachNodeFromKubernetesClusterAction(Params params, CommandExecutionService commandExecutionService)
            throws ActionException {
        super(params, commandExecutionService);
    }

    @Builder
    public DetachNodeFromKubernetesClusterAction(CommandExecutionService commandExecutionService, String host,
                                                 Integer port, String privateKey, String user,
                                                 String nodeName) {
        super(commandExecutionService, host, port, privateKey, user);
        this.nodeName = nodeName;
    }

    @Override
    protected void unpackAdditionalParams(Params params) {
        nodeName = params.getRequiredParam("nodeName");
    }

    @Override
    protected String buildCommand() {
        return String.format("kubectl drain %1$s --ignore-daemonsets --delete-emptydir-data && " +
                "kubectl delete node %1$s", nodeName);
    }
}
