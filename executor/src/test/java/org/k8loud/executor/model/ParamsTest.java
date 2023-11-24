package org.k8loud.executor.model;

import exception.ParamNotFoundException;
import org.junit.jupiter.api.Test;
import org.k8loud.executor.model.Params;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ParamsTest {
    public static final Params PARAMS = new Params(Map.of("key1", "value1", "key2", "value2"));
    public static final String NOT_DEFINED_PARAM = "notDefinedParam";
    public static final String DEFAULT_VALUE = "defaultValue";

    @Test
    public void testRequiredParam() {
        // when
        String value1 = PARAMS.getRequiredParam("key1");
        String value2 = PARAMS.getRequiredParam("key2");

        // then
        assertThat(value1).isEqualTo("value1");
        assertThat(value2).isEqualTo("value2");
    }

    @Test
    public void testRequiredParamNotFound() {
        // when
        Throwable e = catchThrowable(() -> PARAMS.getRequiredParam(NOT_DEFINED_PARAM));

        // then
        assertThat(e).isExactlyInstanceOf(ParamNotFoundException.class)
                .hasMessage("Param '%s' is declared as required and was not found", NOT_DEFINED_PARAM);
    }

    @Test
    public void testOptionalParam() {
        // when
        String value1 = PARAMS.getOptionalParam("key1", DEFAULT_VALUE);
        String value2 = PARAMS.getOptionalParam(NOT_DEFINED_PARAM, DEFAULT_VALUE);

        // then
        assertThat(value1).isEqualTo("value1");
        assertThat(value2).isEqualTo(DEFAULT_VALUE);
    }
}
