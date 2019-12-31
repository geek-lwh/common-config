package com.aha.tech.threadlocal;

import com.dianping.cat.message.spi.internal.DefaultMessageTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: luweihong
 * @Date: 2019/11/28
 */
public class MessageThreadLocal {

    private static final Logger logger = LoggerFactory.getLogger(MessageThreadLocal.class);

    private static InheritableThreadLocal<DefaultMessageTree> inheritableThread = new InheritableThreadLocal<DefaultMessageTree>();

    /**
     * 获取xEnvDto
     * @return
     */
    public static DefaultMessageTree get() {
        return inheritableThread.get();
    }

    /**
     * 设置xEnvDto
     * @param catContext
     */
    public static void set(DefaultMessageTree catContext) {
        inheritableThread.set(catContext);
    }

    /**
     * 清除envDto
     */
    public static void remove() {
        inheritableThread.remove();
    }

}
