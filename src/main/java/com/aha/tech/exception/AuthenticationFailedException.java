package com.aha.tech.exception;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
public class AuthenticationFailedException extends HowlersException {

    public static final String AUTHENTICATION_FAILED_MESSAGE = "该接口不对访客开放";

    public static final Integer AUTHENTICATION_FAILED_CODE = 500500;

    public AuthenticationFailedException() {
        super(AUTHENTICATION_FAILED_MESSAGE, AUTHENTICATION_FAILED_CODE);
    }

    public AuthenticationFailedException(String message) {
        super(message, AUTHENTICATION_FAILED_CODE);
    }

    public AuthenticationFailedException(String msg, int code) {
        super(msg, code);
    }

    public AuthenticationFailedException(String msg, int code, Throwable cause) {
        super(msg, code, cause);
    }

    public AuthenticationFailedException(int code, Throwable cause) {
        super(code, cause);
    }
}
