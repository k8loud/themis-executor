package data;

import exception.ParamNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Params {
    private Map<String, String> params;

    public String getRequiredParam(String param) {
        if (!params.containsKey(param)) {
            throw new ParamNotFoundException(
                    String.format("Param '%s' is declared as required and was not found", param));
        }
        return params.get(param);
    }

    public String getOptionalParam(String param, String defaultValue) {
        return params.getOrDefault(param, defaultValue);
    }
}
