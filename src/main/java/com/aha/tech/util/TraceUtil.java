package com.aha.tech.util;

import com.aha.tech.constant.HeaderConstant;
import com.google.common.collect.Maps;
import io.jaegertracing.internal.Constants;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import io.opentracing.util.GlobalTracer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
public class TraceUtil {

    private static final Logger logger = LoggerFactory.getLogger(TraceUtil.class);

    public static final String CLASS = "class";

    public static final String METHOD = "method";

    public static final String SQL = "sql";

    public static final String ERROR = "error";

    // baggage 前缀
    public static final String BAGGAGE_PREFIX = "uberctx-";

    public static final String BAGGAGE_HEADER_KEY = "jaeger-baggage";


    /**
     * 设置错误信息的tags
     * @param e
     * @return
     */
    public static void setCapturedErrorsTags(Exception e) {
        Span span = GlobalTracer.get().activeSpan();
        Map err = Maps.newHashMapWithExpectedSize(6);
        err.put(Fields.EVENT, ERROR);
        err.put(Fields.MESSAGE, e.getMessage());
        err.put(Fields.ERROR_OBJECT, e);
        err.put(Fields.STACK, e.getStackTrace()[0]);
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

//        baggage(servletRequest, hMap);

        hMap.put(HeaderConstant.UBER_TRACE_ID, traceId);

        return hMap;
    }

    /**
     * 查找baggage item 暂时没有使用
     * @param servletRequest
     * @param hMap
     */
    @Deprecated
    private static void lookupBaggageItem(HttpServletRequest servletRequest, Map<String, String> hMap) {
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String h = headerNames.nextElement();
            if (h.startsWith(BAGGAGE_PREFIX) || h.equals(Constants.BAGGAGE_HEADER_KEY) || h.equals(Constants.DEBUG_ID_HEADER_KEY)) {
                hMap.put(h, servletRequest.getHeader(h));
            }
        }
    }

    /**
     * 设置traceid的tag信息
     * @param span
     */
    public static void setTraceIdTags(Span span) {
        span.setTag(HeaderConstant.TRACE_ID, span.context().toTraceId());
        span.setTag(HeaderConstant.SPAN_ID, span.context().toSpanId());
    }

    /**
     * 设置rpcClient的tag信息
     * @param span
     * @param ip
     * @param serviceName
     * @param port
     */
    public static void setRpcClientTags(Span span, String ip, String serviceName, int port) {
        Tags.PEER_HOST_IPV4.set(span, ip);
        Tags.SPAN_KIND.set(span, Tags.SPAN_KIND_CLIENT);
        Tags.PEER_PORT.set(span, port);
        Tags.PEER_SERVICE.set(span, serviceName);
    }

    /**
     * 设置sql语句的tag信息
     * @param span
     * @param dbInstance
     * @param statement
     */
    public static void setSqlCallsTags(Span span, String dbInstance, String statement) {
        Tags.DB_TYPE.set(span, SQL);
        Tags.DB_INSTANCE.set(span, dbInstance);
        Tags.DB_STATEMENT.set(span, statement);
    }

    /**
     * 异步请求传递trace,指定线程池
     * @param supplier
     * @param executor
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<T> asyncInvoke(Supplier<T> supplier, Executor executor) {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.scopeManager().activeSpan();
        Supplier<T> newSupplier = () -> {
            try (Scope scope = tracer.scopeManager().activate(span)) {
                return supplier.get();
            }
        };

        return CompletableFuture.supplyAsync(newSupplier, executor).exceptionally(t -> {
            setCapturedErrorsTags((Exception) t);
            return null;
        });
    }

    /**
     * 异步请求传递trace
     * @param supplier
     * @param <T>
     * @return
     */
    public static <T> CompletableFuture<T> asyncInvoke(Supplier<T> supplier) {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.scopeManager().activeSpan();
        Supplier<T> newSupplier = () -> {
            try (Scope scope = tracer.scopeManager().activate(span)) {
                return supplier.get();
            }
        };

        return CompletableFuture.supplyAsync(newSupplier).exceptionally(t -> {
            setCapturedErrorsTags((Exception) t);
            return null;
        });
    }

    /**
     * 异步请求传递trace
     * @param runnable
     * @return
     */
    public static CompletableFuture<Void> asyncInvoke(Runnable runnable, Executor executor) {
        // 第一步
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.scopeManager().activeSpan();
        CompletableFuture<Void> future;
        try (Scope scope = tracer.scopeManager().activate(span)) {
            future = CompletableFuture.runAsync(runnable, executor);
            future.exceptionally(t -> {
                setCapturedErrorsTags((Exception) t);
                return null;
            });
        }

        return future;
    }

    /**
     * 异步请求传递trace
     * @param runnable
     * @return
     */
    public static CompletableFuture<Void> asyncInvoke(Runnable runnable) {
        Tracer tracer = GlobalTracer.get();
        Span span = tracer.scopeManager().activeSpan();
        CompletableFuture<Void> future;
        try (Scope scope = tracer.scopeManager().activate(span)) {
            future = CompletableFuture.runAsync(runnable);
            future.exceptionally(t -> {
                setCapturedErrorsTags((Exception) t);
                return null;
            });
        }

        return future;
    }

}
