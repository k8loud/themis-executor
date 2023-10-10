package data;


import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.jetbrains.annotations.TestOnly;

@With
@Builder
@Data
public class ExecutionRQ {
    String collectionName;
    String actionName;
    Params params;

    @TestOnly
    public static ExecutionRQ createExecutionRQ(String collectionName, String actionName, Params params) {
        return ExecutionRQ.builder()
                .collectionName(collectionName)
                .actionName(actionName)
                .params(params)
                .build();
    }
}
