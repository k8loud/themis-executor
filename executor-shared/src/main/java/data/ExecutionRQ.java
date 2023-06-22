package data;


import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.util.Map;

@With
@Builder
@Data
public class ExecutionRQ {
    String collectionName;
    String actionName;
    Map<String, String> params;
}
