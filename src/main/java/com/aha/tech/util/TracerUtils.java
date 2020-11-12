package com.aha.tech.util;

import com.google.common.collect.Maps;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.aha.tech.constant.HeaderConstant.*;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
public class TracerUtils {

    private static final Logger logger = LoggerFactory.getLogger(TracerUtils.class);

    public static final String CLASS = "class";

    public static final String METHOD = "method";

    public static final String SQL = "sql";

    /**
     * 构建一个traceMap 描述错误信息
     * @param e
     * @return
     */
    public static Map errorTraceMap(Exception e) {
        Map err = Maps.newHashMapWithExpectedSize(3);
        err.put(Fields.EVENT, Tags.ERROR.getKey());
        err.put(Fields.ERROR_OBJECT, e);
        err.put(Fields.MESSAGE, e.getMessage());

        return err;
    }

    /**
     * 构建一个跨进程调用的traceMap信息
     * @param serverName
     * @param port
     * @param api
     * @return
     * @throws Exception
     */
    public static Map attachTraceInfoMap(String serverName, int port, String api) {
        Map<String, String> hMap = Maps.newHashMapWithExpectedSize(3);
        String ip = null;
        try {
            ip = IpUtil.getLocalHostAddress();
        } catch (Exception e) {
            logger.error("构建traceInfo时 计算ip地址出错", e);
            e.printStackTrace();
        }
        hMap.put(REQUEST_FROM, serverName);
        hMap.put(REQUEST_IP, ip + ":" + port);
        hMap.put(REQUEST_API, api);

        return hMap;
    }

}
