package org.k8loud.executor.exception.code;

public enum MapperExceptionCode {
    ACTION_CLASS_NOT_FOUND,
    INVALID_CONSTRUCTOR, // The action doesn't have a compatible constructor, should never occur
    NEW_INSTANCE_FAILURE
}
