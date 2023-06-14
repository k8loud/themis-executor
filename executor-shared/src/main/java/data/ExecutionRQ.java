package data;


import lombok.Data;

import java.util.Map;

@Data
public class ExecutionRQ {
    String action;
    Map<String, String> params;
}
