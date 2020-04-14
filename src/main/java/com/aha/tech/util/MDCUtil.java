package com.aha.tech.util;

import com.aha.tech.constant.HeaderConstant;
import com.dianping.cat.Cat;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: luweihong
 * @Date: 2020/4/14
 */
public class MDCUtil {

    public static final String TRACE_ID = "traceId";

    /**
     * 获取traceId
     * @return
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID);
    }

    /**
     * 初始化traceId
     * unsafe
     * @return
     */
    public static String initTraceId(HttpServletRequest request) {
        String tId = request.getHeader(HeaderConstant.TRACE_ID);
        if (StringUtils.isBlank(tId)) {
            tId = Cat.createMessageId();
        }

        return tId;
    }

    /**
     * 获取traceId 如果没有则创建
     * unsafe
     * @return
     */
    public static String getAndSetTraceId(HttpServletRequest request) {
        String tId = initTraceId(request);
        MDC.put(TRACE_ID, tId);

        return tId;
    }

}
