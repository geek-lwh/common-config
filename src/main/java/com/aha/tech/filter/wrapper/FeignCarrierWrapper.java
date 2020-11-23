package com.aha.tech.filter.wrapper;

import feign.RequestTemplate;
import io.opentracing.propagation.TextMap;

import java.util.Iterator;
import java.util.Map;

/**
 * @Author: luweihong
 * @Date: 2020/11/13
 *
 * 用于trace inject时的封装
 *
 */
public class FeignCarrierWrapper implements TextMap {

    public RequestTemplate requestTemplate;

    public FeignCarrierWrapper(RequestTemplate requestTemplate) {
        this.requestTemplate = requestTemplate;
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        throw new UnsupportedOperationException("carrier is write-only");
    }

    @Override
    public void put(String key, String value) {
        requestTemplate.header(key, value);
    }

}
