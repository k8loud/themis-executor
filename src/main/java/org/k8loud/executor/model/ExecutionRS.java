package org.k8loud.executor.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.Map;

@With
@Builder
@Data
public class ExecutionRS {
    String result;
    ExecutionExitCode exitCode;
    Map<String, Object> additionalData;
}
