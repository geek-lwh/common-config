package com.aha.tech.util;

import com.aha.tech.constant.HeaderConstant;
import com.google.common.collect.Maps;
import io.opentracing.Span;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
public class TracerUtils {

    private static final Logger logger = LoggerFactory.getLogger(TracerUtils.class);

    public static final String CLASS = "class";

    public static final String METHOD = "method";

    public static final String SQL = "sql";

    // baggage 前缀
    public static final String BAGGAGE_PREFIX = "uberctx-";

    public static final String BAGGAGE_HEADER_KEY = "jaeger-baggage";


    /**
     * 上报一个error在trace中
     * @param e
     * @return
     */
    public static void reportErrorTrace(Exception e) {
        Span span = GlobalTracer.get().activeSpan();
        Map err = Maps.newHashMapWithExpectedSize(6);
        err.put(Fields.EVENT, Tags.ERROR.getKey());
        err.put(Fields.ERROR_OBJECT, e);
        err.put(Fields.MESSAGE, e.getMessage());
        Tags.ERROR.set(span, true);
        span.log(err);
        logger.error(e.getMessage(), e);
    }

    /**
     * 从header中解析相关的trace信息,优化默认方法
     * 暂时禁用baggage item 所以自定义方法 免去所有header的判断
     * @param servletRequest
     * @return
     */
    public static Map parseTraceContext(HttpServletRequest servletRequest) {
        Map<String, String> hMap = Maps.newHashMap();

        String traceId = servletRequest.getHeader(HeaderConstant.UBER_TRACE_ID);
        if (StringUtils.isBlank(traceId)) {
            return hMap;
        }

//        Enumeration<String> headerNames = servletRequest.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String h = headerNames.nextElement();
//            if (h.startsWith(BAGGAGE_PREFIX) || h.equals(Constants.BAGGAGE_HEADER_KEY) || h.equals(Constants.DEBUG_ID_HEADER_KEY)) {
//                hMap.put(h, servletRequest.getHeader(h));
//            }
//        }

        hMap.put(HeaderConstant.UBER_TRACE_ID, traceId);

        return hMap;
    }

    public static void setErrorTag() {

    }

    /**
     * 设置每个tace中span的线索
     * @param span
     */
    public static void setClue(Span span) {
        span.setTag(HeaderConstant.TRACE_ID, span.context().toTraceId());
        span.setTag(HeaderConstant.SPAN_ID, span.context().toSpanId());
    }

}
