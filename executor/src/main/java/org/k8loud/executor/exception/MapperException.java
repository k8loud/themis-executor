package org.k8loud.executor.exception;

import org.k8loud.executor.exception.code.MapperExceptionCode;

public class MapperException extends CustomException {
    public MapperException(Exception e, MapperExceptionCode errorCode) {
        super(e, errorCode);
    }
}
