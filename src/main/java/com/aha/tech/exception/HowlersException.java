package com.aha.tech.exception;


/**
 * 低级错误
 * 抛出warn级别
 */
public class HowlersException extends GlobalException {

    public HowlersException(String msg, int code) {
        super(msg, code);
    }

    public HowlersException(String msg, int code, Throwable cause) {
        super(msg, code, cause);
    }

    public HowlersException(int code, Throwable cause) {
        super(code, cause);
    }

}
