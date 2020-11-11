package com.aha.tech.threadlocal;

import com.aha.tech.filter.tracer.CatContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class CatContextThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(CatContextThreadLocal.class);

    private static InheritableThreadLocal<CatContext> inheritableThread = new InheritableThreadLocal<CatContext>();

    /**
     * 获取xEnvDto
     * @return
     */
    public static CatContext get() {
        return inheritableThread.get();
    }

    /**
     * 设置xEnvDto
     * @param catContext
     */
    public static void set(CatContext catContext) {
        inheritableThread.set(catContext);
    }

    /**
     * 清除envDto
     */
    public static void remove() {
        inheritableThread.remove();
    }

}
