package com.aha.tech.util;

import com.google.common.collect.Maps;
import io.opentracing.log.Fields;
import io.opentracing.tag.Tags;

import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/11
 */
public class TracerUtils {

    public static final String CLASS_METHOD = "class.method";

    public static final String CLASS = "class";

    public static final String METHOD = "method";

    public static final String SQL = "sql";


    public static Map errorTraceMap(Exception e) {
        Map err = Maps.newHashMapWithExpectedSize(3);
        err.put(Fields.EVENT, Tags.ERROR.getKey());
        err.put(Fields.ERROR_OBJECT, e);
        err.put(Fields.MESSAGE, e.getMessage());

        return err;
    }
}
