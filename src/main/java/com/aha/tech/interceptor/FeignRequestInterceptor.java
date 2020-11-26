package com.aha.tech.interceptor;

import com.aha.tech.annotation.XEnv;
import com.aha.tech.constant.HeaderConstant;
import com.aha.tech.filter.wrapper.FeignCarrierWrapper;
import com.aha.tech.model.XEnvDto;
import com.aha.tech.threadlocal.XEnvThreadLocal;
import com.aha.tech.util.IpUtil;
import com.aha.tech.util.TraceUtils;
import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.google.common.collect.Lists;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.util.GlobalTracer;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.List;

import static com.aha.tech.constant.HeaderConstant.*;

/**
 * @Author: luweihong
 * @Date: 2018/12/3
 * <p>
 * feign request 拦截器
 * 用于动态传递header信息,如果有需要需要自己定义
 */
public class FeignRequestInterceptor implements RequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(FeignRequestInterceptor.class);

    private static Config config = ConfigService.getAppConfig();

    private static Boolean feignLog = config.getBooleanProperty("feign.log", Boolean.FALSE);

    private static String serverName = config.getProperty("spring.application.name", "UNKNOWN");

    private static int port = config.getIntProperty("common.server.tomcat.port", 0);

    @Override
    public void apply(RequestTemplate requestTemplate) {
        initRequestHeader(requestTemplate);
        overwriteXEnv(requestTemplate);
        Tracer tracer = GlobalTracer.get();
        if (tracer != null) {
            Span span = tracer.activeSpan();
            SpanContext spanContext = span.context();
            TraceUtils.setClue(span);
            requestTemplate.header(HeaderConstant.TRACE_ID, spanContext.toTraceId());
            requestTemplate.header(HeaderConstant.SPAN_ID, spanContext.toSpanId());
            tracer.inject(spanContext, Format.Builtin.HTTP_HEADERS, new FeignCarrierWrapper(requestTemplate));
        }
        if (feignLog) {
            feignRequestLogging(requestTemplate);
        }
    }

    /**
     * 覆盖XEnv的值
     * @param requestTemplate
     */
    private void overwriteXEnv(RequestTemplate requestTemplate) {
        XEnvDto xEnvDto = XEnvThreadLocal.get();
        if (xEnvDto != null) {
            initHeaderFromEnv(xEnvDto, requestTemplate);
        }
    }

    /**
     * 从xEnvDto中解析值到feign requestHeader
     * @param xEnvDto
     * @param requestTemplate
     */
    private void initHeaderFromEnv(XEnvDto xEnvDto, RequestTemplate requestTemplate) {
        try {
            Field[] xEnvFields = xEnvDto.getClass().getDeclaredFields();
            for (Field field : xEnvFields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(XEnv.class)) {
                    XEnv annotation = field.getAnnotation(XEnv.class);
                    String headerName = annotation.value();
                    PropertyDescriptor pd = new PropertyDescriptor(field.getName(), xEnvDto.getClass());
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        Object getValue = readMethod.invoke(xEnvDto);
                        if (getValue != null) {
                            requestTemplate.header(headerName, getValue.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 初始化请求头
     * @param requestTemplate
     */
    private void initRequestHeader(RequestTemplate requestTemplate) {
        if (!requestTemplate.headers().containsKey(CONTENT_TYPE)) {
            String contentType = requestTemplate.method().equals(HttpMethod.POST.name()) ? MediaType.APPLICATION_JSON_UTF8_VALUE : MediaType.TEXT_PLAIN_VALUE;
            requestTemplate.header(CONTENT_TYPE, contentType);
        }

        List<String> acceptableMediaTypes = Lists.newArrayList(MediaType.ALL_VALUE);
        requestTemplate.header(HeaderConstant.ACCEPT, acceptableMediaTypes);
        requestTemplate.header(HeaderConstant.CONNECTION, HTTP_HEADER_CONNECTION_VALUE);
        String keepAlive = config.getProperty("common.http.keep.alive.timeout", HTTP_HEADER_KEEP_ALIVE_VALUE);
        requestTemplate.header(HeaderConstant.HTTP_HEADER_KEEP_ALIVE_KEY, keepAlive);
        requestTemplate.header(HeaderConstant.HTTP_HEADER_X_REQUESTED_WITH_KEY, HTTP_HEADER_X_REQUESTED_WITH_VALUE);
        requestTemplate.header(HeaderConstant.CONTENT_ENCODING, CHARSET_ENCODING);
        requestTemplate.header(HeaderConstant.X_TOKEN_KEY, X_TOKEN);
        requestTemplate.header(REQUEST_FROM, serverName);
        requestTemplate.header(REQUEST_API, requestTemplate.url());
        try {
            requestTemplate.header(REQUEST_ADDRESS, IpUtil.getLocalHostAddress() + ":" + port);
        } catch (Exception e) {
            logger.error("构建traceInfo时 计算ip地址出错", e);
        }
    }

    /**
     * feign调用日志
     * @param requestTemplate
     */
    private void feignRequestLogging(RequestTemplate requestTemplate) {
        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append("Feign request URI : ").append(requestTemplate.url()).append(requestTemplate.queryLine()).append(System.lineSeparator());
        sb.append("Feign request HEADER : ").append(requestTemplate.headers().toString()).append(System.lineSeparator());
        String body = requestTemplate.body() == null ? Strings.EMPTY : new String(requestTemplate.body(), Charset.forName("utf-8"));
        sb.append("Feign request BODY : ").append(body);

        logger.info("Feign request INFO : {}", sb);
    }

}
