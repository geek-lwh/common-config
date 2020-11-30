package com.aha.tech.threadlocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class RequestLogThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogThreadLocal.class);

    private static ThreadLocal<String> inheritableThread = new ThreadLocal<>();

    /**
     * 获取xEnvDto
     * @return
     */
    public static String get() {
        return inheritableThread.get();
    }

    /**
     * 设置xEnvDto
     * @param requestLog
     */
    public static void set(String requestLog) {
        inheritableThread.set(requestLog);
    }

    /**
     * 清除envDto
     */
    public static void remove() {
        inheritableThread.remove();
    }

}
