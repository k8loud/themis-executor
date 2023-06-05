package data;


import lombok.Data;

import java.util.Map;

@Data
public class ActionRequest {

    String action;
    Map<String, String> params;
}
