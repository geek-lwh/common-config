package com.aha.tech.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/30
 *
 * response code 工具类
 */
public class RepsonseCodeUtil {

    // 应用前缀
    public static Map<String, Integer> APPLICATION_CODE_PREFIX = ImmutableMap.<String, Integer>builder()
            .put("userbff", 70)
            .put("userserver", 16)
            .put("passportserver", 20)
            .put("accountserver", 18)
            .put("distributionserver", 12)
            .put("msgserver", 11)
            .put("liveserver", 17)
            .put("policyserver", 21)
            .put("orderserver", 10)
            .put("withdrawserver", 13)
            .put("accountserver", 19)
            .put("appbff", 73)
            .put("orderbff", 72)
            .put("payserver", 14)
            .put("memberserver", 22)
            .put("msgpushserver", 24)
            .put("storyserver", 25)
            .put("operationserver", 26)
            .put("operationbff", 27)
            .put("gameserver", 28)
            .put("bidataserver", 29)
            .put("gpsserver", 30)
            .build();

    /**
     * 获取应用前缀
     * @param application
     * @return
     */
    public static Integer getApplictionPrefix(String application) {
        return APPLICATION_CODE_PREFIX.getOrDefault(application, 0);
    }
}
