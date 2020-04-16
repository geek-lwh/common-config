package com.aha.tech.constant;

import org.springframework.core.Ordered;

/**
 * @Author: luweihong
 * @Date: 2020/4/16
 */
public class OrderedConstant {

    public static final int CROSS_DOMAIN_REQUEST_FILTER_ORDERED = Ordered.LOWEST_PRECEDENCE + 1;

    public static final int REQUEST_RESPONSE_FILTER_ORDERED = CROSS_DOMAIN_REQUEST_FILTER_ORDERED + 1;

    public static final int CAT_CONTEXT_FILTER_ORDERED = REQUEST_RESPONSE_FILTER_ORDERED + 1;


}
