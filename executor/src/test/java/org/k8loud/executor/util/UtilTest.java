package org.k8loud.executor.util;

import org.apache.commons.net.util.SubnetUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilTest {
    @Test
    void getDifferenceCIDRS() {
        // given
        String baseSubnet = "192.168.1.0/24";
        String subnetToExclude = "192.168.1.160/27";
        List<String> expectedResult = List.of("192.168.1.0/27", "192.168.1.32/27", "192.168.1.64/27", "192.168.1.96/27",
                "192.168.1.128/27", "192.168.1.192/27", "192.168.1.224/27");

        // when
        List<SubnetUtils> result = Util.getDifferenceCIDRS(baseSubnet, subnetToExclude);

        // then
        assertThat(result).extracting(SubnetUtils::getInfo)
                .extracting(SubnetUtils.SubnetInfo::getCidrSignature)
                .containsExactlyElementsOf(expectedResult);
    }
}
