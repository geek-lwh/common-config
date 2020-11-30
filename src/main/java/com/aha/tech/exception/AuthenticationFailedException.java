package com.aha.tech.exception;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
public class AuthenticationFailedException extends HowlersException {

    public static final String AUTHENTICATION_FAILED_MESSAGE = "该接口不对访客开放";

    public static final Integer AUTHENTICATION_FAILED_CODE = 500500;

    public AuthenticationFailedException(String message) {
        super(AUTHENTICATION_FAILED_CODE, message);
    }

}
