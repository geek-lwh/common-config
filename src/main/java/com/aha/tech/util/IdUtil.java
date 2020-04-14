package com.aha.tech.util;

import com.dianping.cat.Cat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

/**
 * @Author: luweihong
 * @Date: 2020/4/14
 */
public class IdUtil {

    public static final String TRACE_ID = "traceId";

    /**
     * 获取traceId
     * @return
     */
    public static String getTraceId() {
        String tId = MDC.get(TRACE_ID);
        return tId;
    }

    /**
     * 获取traceId 如果没有则创建
     * @return
     */
    public static String getAndSetTraceId() {
        String tId = getTraceId();
        if (StringUtils.isBlank(tId)) {
            tId = Cat.createMessageId();
        }

        return tId;
    }
}
