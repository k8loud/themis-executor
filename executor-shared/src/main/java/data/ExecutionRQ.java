package data;


import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;

@With
@Builder
@Data
public class ExecutionRQ {
    String collectionName;
    String actionName;
    Map<String, String> params;

    @TestOnly
    public static ExecutionRQ createExecutionRQ(String collectionName, String actionName, Map<String, String> params) {
        return ExecutionRQ.builder()
                .collectionName(collectionName)
                .actionName(actionName)
                .params(params)
                .build();
    }
}
