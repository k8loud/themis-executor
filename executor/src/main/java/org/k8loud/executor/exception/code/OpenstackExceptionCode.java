package org.k8loud.executor.exception.code;

import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.model.compute.Action;

public enum OpenstackExceptionCode {
    TOKEN_INVALID,
    AUTHENTICATION_ERROR,
    SERVER_NOT_EXISTS,
    FLAVOR_NOT_EXITS,
    VOLUME_NOT_EXITS,
    RESIZE_SERVER_FAILED,
    FLAVORS_DISKS_NOT_SAME,
    FLAVORS_COMPARISON,
    COPY_SERVER_FAILED,
    ATTACH_VOLUME_FAILED,
    VOLUME_ERROR,
    DETACH_VOLUME_FAILED,
    PAUSE_SERVER_FAILED,
    UNPAUSE_SERVER_FAILED,
    UNSUPPORTED_ACTION;

    public static OpenstackExceptionCode getNovaExceptionCode(Action action) throws OpenstackException {
        return switch (action){
            case PAUSE -> PAUSE_SERVER_FAILED;
            case UNPAUSE -> UNPAUSE_SERVER_FAILED;
            default -> throw new OpenstackException(action.name() + " not supported", UNSUPPORTED_ACTION);
        };
    }
}
