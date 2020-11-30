package com.aha.tech.exception;


/**
 * 严重错误
 * 抛出error级别
 */
public class SeriousException extends GlobalException {

    public SeriousException(String msg, int code) {
        super(msg, code);
    }

    public SeriousException(String msg, int code, Throwable cause) {
        super(msg, code, cause);
    }

    public SeriousException(int code, Throwable cause) {
        super(code, cause);
    }

}
