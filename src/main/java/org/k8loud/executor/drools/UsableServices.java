package org.k8loud.executor.drools;

import lombok.Getter;
import org.k8loud.executor.cnapp.sockshop.SockShopService;
import org.k8loud.executor.command.CommandExecutionService;
import org.k8loud.executor.kubernetes.KubernetesService;
import org.k8loud.executor.openstack.OpenstackService;
import org.springframework.stereotype.Component;

@Getter
@Component
public class UsableServices {
    SockShopService sockShopService;
    CommandExecutionService commandExecutionService;
    KubernetesService kubernetesService;
    OpenstackService openstackService;

    CronCheckerService cronCheckerService;
}
