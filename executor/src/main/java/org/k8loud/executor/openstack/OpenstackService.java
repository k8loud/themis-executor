package org.k8loud.executor.openstack;

import org.k8loud.executor.exception.OpenstackException;
import org.k8loud.executor.util.annotation.ThrowExceptionAndLogExecutionTime;

public interface OpenstackService {
    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "RESIZE_SERVER_FAILED")
    void resizeServer(String region, String serverId, String newFlavorId) throws OpenstackException;

    @ThrowExceptionAndLogExecutionTime(exceptionClass = "OpenstackException", exceptionCode = "COPY_SERVER_FAILED")
    void copyServer(String region, String serverId) throws OpenstackException;

}
