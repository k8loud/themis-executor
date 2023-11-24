package org.k8loud.executor.util;

import io.fabric8.kubernetes.client.utils.Utils;
import org.apache.commons.net.util.SubnetUtils;
import org.k8loud.executor.exception.ValidationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.k8loud.executor.exception.code.ValidationExceptionCode.ADDITIONAL_DATA_WRONG_KEY;

public final class Util {
    private Util() {
        // Meant for static methods only, don't instantiate
    }

    public static boolean emptyOrBlank(String s) {
        return s == null || s.isBlank();
    }

    public static String nameWithUuid(String s) {
        return String.format("%s-%s", s, UUID.randomUUID().toString().substring(0, 8));
    }

    public static String getFullResourceName(String resourceType, String resourceName, String delimiter) {
        return String.format("%s%s%s", resourceType, delimiter, resourceName);
    }

    public static String getFullResourceName(String resourceType, String resourceName) {
        return getFullResourceName(resourceType, resourceName, "/");
    }

    public static Map<String, String> resultMap(String result) {
        return new HashMap<>(Map.of("result", result));
    }

    public static Map<String, String> resultMap(String result,
                                                Map<String, String> additionalData) throws ValidationException {
        if (additionalData.containsKey("result")) {
            throw new ValidationException(ADDITIONAL_DATA_WRONG_KEY);
        }

        Map<String, String> resultMap = new HashMap<>(additionalData);
        resultMap.put("result", result);
        return resultMap;
    }

    public static boolean hasCommonSubnet(String subnet1, String subnet2) {
        if (Utils.isNullOrEmpty(subnet1) || Utils.isNullOrEmpty(subnet2)){
            return false;
        }

        List<String> addresses1 = List.of(new SubnetUtils(subnet1).getInfo().getAllAddresses());
        Set<String> addresses2 = new HashSet<>(List.of(new SubnetUtils(subnet2).getInfo().getAllAddresses()));
        return addresses1.stream().anyMatch(addresses2::contains);
    }

    public static List<SubnetUtils> getDifferenceCIDRS(String baseSubnet, String subnetToExclude) {
        int baseMask = Integer.parseInt(baseSubnet.split("/")[1]);
        int excludeMask = Integer.parseInt(subnetToExclude.split("/")[1]);

        if (!hasCommonSubnet(baseSubnet, subnetToExclude) || excludeMask == baseMask) {
            return List.of(new SubnetUtils(baseSubnet));
        } else if (excludeMask < baseMask) {
            return Collections.emptyList();
        }

        String baseAddress = new SubnetUtils(baseSubnet).getInfo().getNetworkAddress();
        List<List<Integer>> oktets = new ArrayList<>();
        Arrays.stream(baseAddress.split("\\."))
                .forEach(oktet -> oktets.add(listOfBits(oktet)));

        List<SubnetUtils> result = new ArrayList<>();
        bitsCombinations(excludeMask - baseMask).forEach(combination -> {
            IntStream.range(baseMask, excludeMask).forEach(i -> {
                oktets.get(i / 8).set(i % 8, combination.get(i - baseMask));
            });
            String subnet = ipFromOktets(oktets) + "/" + excludeMask;
            if (!subnet.equals(subnetToExclude)){
                result.add(new SubnetUtils(subnet));
            }
        });

        return result;
    }

    private static List<Integer> listOfBits(String oktet) {
        String binaryString = Integer.toBinaryString(Integer.parseInt(oktet));

        List<Integer> bitsArray = new ArrayList<>(Collections.nCopies(8, 0));
        int move = 8 - binaryString.length();
        for (int i = 0; i < binaryString.length(); i++) {
            bitsArray.set(i + move, Integer.parseInt(String.valueOf(binaryString.charAt(i))));
        }
        return bitsArray;
    }

    private static String ipFromOktets(List<List<Integer>> oktets) {
        return oktets.stream()
                .map(oktet -> {
                    String oktetStr = oktet.stream()
                            .map(Object::toString)
                            .collect(Collectors.joining());
                    return String.valueOf(Integer.parseInt(oktetStr, 2));
                })
                .collect(Collectors.joining("."));
    }

    private static List<List<Integer>> bitsCombinations(int len) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> start = new ArrayList<>(Collections.nCopies(len, 0));

        combine(start, len, 0, result);

        return result;
    }

    private static void combine(List<Integer> current, int len, int k, List<List<Integer>> result) {
        if (k >= len) {
            result.add(new ArrayList<>(current));
            return;
        }

        combine(current, len, k + 1, result);
        List<Integer> kChanged = new ArrayList<>(current);
        kChanged.set(k, 1);
        combine(kChanged, len, k + 1, result);
    }
}
