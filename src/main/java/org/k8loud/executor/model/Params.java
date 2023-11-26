package org.k8loud.executor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.k8loud.executor.exception.ParamNotFoundException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public Date getOptionalParamAsDate(String param, Date defaultValue, SimpleDateFormat dateFormatter) {
        if (params.containsKey(param)) {
            try {
                return dateFormatter.parse(params.get(param));
            } catch (ParseException ignored) {
            }
        }
        return defaultValue;
    }

    public Integer getRequiredParamAsInteger(String param) {
        return Integer.parseInt(getRequiredParam(param));
    }

    public Long getOptionalParamAsLong(String param, Long defaultValue) {
        return Optional.ofNullable(params.get(param)).map(Long::parseLong).orElse(defaultValue);
    }

    public Integer getOptionalParamAsInt(String param, Integer defaultValue) {
        return Optional.ofNullable(params.get(param)).map(Integer::parseInt).orElse(defaultValue);
    }

    public boolean getOptionalParamAsBoolean(String param, String defaultValue) {
        return Boolean.parseBoolean(getOptionalParam(param, defaultValue));
    }

    public List<String> getOptionalParamAsListOfStrings(String param, List<String> defaultValue) {
        return Optional.ofNullable(params.get(param))
                .map(p -> Arrays.stream(p.split(",")).toList())
                .orElse(defaultValue);
    }
}
