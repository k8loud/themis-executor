package org.k8loud.executor.util;

import io.fabric8.kubernetes.client.utils.Utils;
import org.apache.commons.net.util.SubnetUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubnetHelper {
    public static boolean hasCommonSubnet(String subnet1, String subnet2) {
        if (Utils.isNullOrEmpty(subnet1) || Utils.isNullOrEmpty(subnet2)) {
            return false;
        } else if (subnet1.equals(subnet2)) {
            return true;
        }

        return hasCommonAddress(new SubnetUtils(subnet1).getInfo(), new SubnetUtils(subnet2).getInfo());
    }

    public static List<SubnetUtils> getDifferenceCIDRS(String baseSubnet, String subnetToExclude) {
        int baseMask = Integer.parseInt(baseSubnet.split("/")[1]);
        int excludeMask = Integer.parseInt(subnetToExclude.split("/")[1]);

        if (excludeMask < baseMask || baseSubnet.equals(subnetToExclude)) {
            return Collections.emptyList();
        } else if (!hasCommonSubnet(baseSubnet, subnetToExclude)) {
            return List.of(new SubnetUtils(baseSubnet));
        }

        return createCIDRSDifference(new SubnetUtils(baseSubnet), new SubnetUtils(subnetToExclude));
    }

    private static List<List<Integer>> getSubnetBitOctets(String address) {
        List<List<Integer>> subnetOctets = new ArrayList<>();
        Arrays.stream(address.split("\\."))
                .forEach(octet -> subnetOctets.add(octetToBits(octet)));
        return subnetOctets;
    }

    private static List<List<Integer>> getSubnetBitOctets(String address, int bitToChange) {
        List<List<Integer>> subnetOctets = getSubnetBitOctets(address);
        List<Integer> octetToChange = subnetOctets.get(bitToChange / 8);
        octetToChange.set(bitToChange % 8, (octetToChange.get(bitToChange % 8) + 1) % 2);
        return subnetOctets;
    }

    private static boolean hasCommonAddress(SubnetUtils.SubnetInfo subnetInfo1, SubnetUtils.SubnetInfo subnetInfo2) {
        return subnetInfo1.isInRange(subnetInfo2.getNetworkAddress()) ||
                subnetInfo2.isInRange(subnetInfo1.getNetworkAddress());
    }

    private static List<SubnetUtils> createCIDRSDifference(SubnetUtils baseSubnet, SubnetUtils subnetToExclude) {
        return createCIDRSDifference(baseSubnet.getInfo(), subnetToExclude.getInfo(), new ArrayList<>());
    }

    private static List<SubnetUtils> createCIDRSDifference(SubnetUtils.SubnetInfo baseSubnet,
                                                           SubnetUtils.SubnetInfo subnetToExclude,
                                                           List<SubnetUtils> result) {
        int baseMask = getMask(baseSubnet);
        int newMask = baseMask + 1;
        if (newMask > getMask(subnetToExclude)) {
            return result;
        }

        List<List<Integer>> baseSubnetOctets = getSubnetBitOctets(baseSubnet.getNetworkAddress());
        String subnet1 = ipFromOctets(baseSubnetOctets) + "/" + newMask;

        List<List<Integer>> secondSubnetOctets = getSubnetBitOctets(baseSubnet.getNetworkAddress(), baseMask);
        String subnet2 = ipFromOctets(secondSubnetOctets) + "/" + newMask;

        Stream.of(subnet1, subnet2).forEach(s -> {
            if (hasCommonSubnet(s, subnetToExclude.getCidrSignature())) {
                createCIDRSDifference(new SubnetUtils(s).getInfo(), subnetToExclude, result);
            } else {
                result.add(new SubnetUtils(s));
            }
        });

        return result;
    }

    private static List<Integer> octetToBits(String octet) {
        String binaryString = Integer.toBinaryString(Integer.parseInt(octet));

        List<Integer> bitsArray = new ArrayList<>(Collections.nCopies(8, 0));
        int move = 8 - binaryString.length();
        for (int i = 0; i < binaryString.length(); i++) {
            bitsArray.set(i + move, Integer.parseInt(String.valueOf(binaryString.charAt(i))));
        }
        return bitsArray;
    }

    private static int getMask(SubnetUtils.SubnetInfo subnet) {
        return Integer.parseInt(subnet.getCidrSignature().split("/")[1]);
    }

    private static String ipFromOctets(List<List<Integer>> octets) {
        return octets.stream()
                .map(octet -> {
                    String octetStr = octet.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining());
                    return String.valueOf(Integer.parseInt(octetStr, 2));
                })
                .collect(Collectors.joining("."));
    }
}
