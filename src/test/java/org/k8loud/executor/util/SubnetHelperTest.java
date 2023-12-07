package org.k8loud.executor.util;

import org.apache.commons.net.util.SubnetUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SubnetHelperTest {
    @Test
    void getDifferenceCIDRS() {
        // given
        String baseSubnet = "1.1.0.0/16";
        String subnetToExclude = "1.1.1.0/24";
        List<String> expectedResult = List.of("1.1.128.0/17", "1.1.64.0/18", "1.1.32.0/19", "1.1.16.0/20", "1.1.8.0/21",
                "1.1.4.0/22", "1.1.2.0/23", "1.1.0.0/24");

        // when
        List<SubnetUtils> result = SubnetHelper.getDifferenceCIDRS(baseSubnet, subnetToExclude);

        // then
        assertThat(result).extracting(SubnetUtils::getInfo)
                .extracting(SubnetUtils.SubnetInfo::getCidrSignature)
                .containsExactlyInAnyOrderElementsOf(expectedResult);
    }
}
