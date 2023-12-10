package org.k8loud.executor.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExecutionRS {
    String result;
    ExecutionExitCode exitCode;
    Map<String, Object> additionalData;
}
