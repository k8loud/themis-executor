package data;

import exception.ParamNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class Params {
    @Getter
    private final Map<String, String> params;

    public String getRequiredParam(String param) {
        if (!params.containsKey(param)) {
            throw new ParamNotFoundException(String.format("Param '%s' is declared as required and was not found", param));
        }
        return params.get(param);
    }

    public String getOptionalParam(String param, String defaultValue) {
        return params.getOrDefault(param, defaultValue);
    }
}
