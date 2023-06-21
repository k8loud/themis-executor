package data;

import lombok.Data;

@Data
public class ExecutionRS {
    String result;
    ExecutionExitCode exitCode;
}
