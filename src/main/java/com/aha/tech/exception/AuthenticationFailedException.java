package com.aha.tech.exception;

/**
 * @Author: luweihong
 * @Date: 2019/6/10
 */
public class AuthenticationFailedException extends HowlersException {

    public AuthenticationFailedException() {
        super("该接口不对访客开放", 500500);
    }

    public AuthenticationFailedException(String api) {
        super("该接口不对访客开放", 500500);
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
