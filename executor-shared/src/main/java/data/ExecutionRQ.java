package data;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Builder
@Data
public class ExecutionRQ {
    String collectionName;
    String actionName;
    Map<String, String> params;
}
