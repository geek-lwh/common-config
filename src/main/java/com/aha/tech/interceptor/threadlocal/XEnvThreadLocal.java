package com.aha.tech.interceptor.threadlocal;

import com.aha.tech.model.XEnvDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class XEnvThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(XEnvThreadLocal.class);

    private static InheritableThreadLocal<XEnvDto> inheritableHeadEnv = new InheritableThreadLocal<XEnvDto>();

    /**
     * 获取xEnvDto
     * @return
     */
    public static XEnvDto get() {
        return inheritableHeadEnv.get();
    }

    /**
     * 设置xEnvDto
     * @param xEnvDto
     */
    public static void set(XEnvDto xEnvDto) {
        inheritableHeadEnv.set(xEnvDto);
    }

    /**
     * 清除envDto
     */
    public static void remove() {
        inheritableHeadEnv.remove();
    }

}
