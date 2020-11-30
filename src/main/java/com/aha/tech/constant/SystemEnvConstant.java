package com.aha.tech.constant;


import com.aha.tech.util.PropertyUtil;

/**
 * @Author: luweihong
 * @Date: 2020/4/16
 */
public class SystemEnvConstant {

    public static final String APPLICATION_NAME = PropertyUtil.getApplicationPropValue("spring.application.name").toString();


}
