package org.k8loud.executor.model;

import org.k8loud.executor.exception.ParamNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
        return Optional.ofNullable(params.get(param)).map(Long::parseLong).orElseGet(() -> defaultValue);
    }

    public Integer getOptionalParamAsInt(String param, Integer defaultValue) {
        return Optional.ofNullable(params.get(param)).map(Integer::parseInt).orElseGet(() -> defaultValue);
    }
      
    public boolean getOptionalParamAsBoolean(String param, String defaultValue) {
        return Boolean.parseBoolean(getOptionalParam(param, defaultValue));
    }
}