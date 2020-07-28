package com.aha.tech.constant;

import com.dianping.cat.CatConstants;

/**
 * @Author: luweihong
 * @Date: 2019/12/30
 */
public class CatConstant extends CatConstants {

    public static final String CROSS_CONSUMER = "PigeonCall";

    /**
     * Cross报表中的数据标识
     */
    public static final String CROSS_SERVER = "PigeonService";


    // 调用方
    public static final String CONSUMER_CALL_SERVER = "PigeonCall.server";

    // 调用方
    public static final String CONSUMER_CALL_APP = "PigeonCall.app";

    // 调用方
    public static final String CONSUMER_CALL_PORT = "PigeonCall.port";


    public static final String PROVIDER_APPLICATION_NAME = "serverApplicationName";

    public static final String PROVIDER_APPLICATION_IP = "serverApplicationIp";


    // 接收方
    public static final String PROVIDER_CALL_APP = "PigeonService.app";

    // 接收方
    public static final String PROVIDER_CALL_SERVER = "PigeonService.client";

    public static final String PROVIDER_TRACE_ID = "PigeonService.tId";

    // 接收方
    public static final String PROVIDER_CALL_IP = "PigeonService.ip";

    public static final String FORK_MESSAGE_ID = "m_forkedMessageId";

    public static final String FORK_ROOT_MESSAGE_ID = "m_rootMessageId";

    public static final String FORK_PARENT_MESSAGE_ID = "m_parentMessageId";

    public static final String INTERFACE_NAME = "interfaceName";

    /**
     * Type 常量
     */
    public static final String Type_URL_METHOD = "URL.method";
    public static final String Type_URL_CLIENT = "URL.client";
    public static final String Type_URL_FORWORD = "URL.forword";

    public static final String Type_Service = "Service";
    public static final String Type_Service_METHOD = "Service.method";
    public static final String Type_Service_CLIENT = "Service.client";

    public static final String Type_SQL = "SQL";
    public static final String Type_SQL_METHOD = "SQL.method";
    public static final String Type_SQL_CLIENT = "SQL.client";

    public static final String Type_Cache = "Cache";
    public static final String Type_Cache_METHOD = "Cache.method";
    public static final String Type_Cache_CLIENT = "Cache.client";

    public static final String Type_Call = "Call";
    public static final String Type_Call_METHOD = "Call.method";
    public static final String Type_Call_CLIENT = "Call.client";

    /**
     * http header 常量
     */
    public static final String CAT_HTTP_HEADER_ROOT_MESSAGE_ID = "X-CAT-ROOT-MESSAGE-ID";
    public static final String CAT_HTTP_HEADER_PARENT_MESSAGE_ID = "X-CAT-ROOT-PARENT-ID";
    public static final String CAT_HTTP_HEADER_CHILD_MESSAGE_ID = "X-CAT-ROOT-CHILD-ID";
    public static final String UNKNOWN_SERVER = "UNKNOWN_SERVER";
}
