package data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ExecutionRS {
    String result;
    ExecutionExitCode exitCode;
    Map<String, String> additionalData;
}
