package com.aha.tech.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import io.opentracing.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class TraceThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(TraceThreadLocal.class);

    private static TransmittableThreadLocal<Span> transmittableThreadLocal = new TransmittableThreadLocal<>();

    /**
     * get value
     * @return
     */
    public static Span get() {
        return transmittableThreadLocal.get();
    }

    /**
     * set value
     * @param span
     */
    public static void set(Span span) {
        transmittableThreadLocal.set(span);
    }

    /**
     * clear
     */
    public static void remove() {
        transmittableThreadLocal.remove();
    }

}
