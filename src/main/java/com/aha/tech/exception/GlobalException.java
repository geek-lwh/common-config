package com.aha.tech.exception;


/**
 * 说明:异常类父类,用于抛出异常动态转型, 此abstract异常类不可构造使用
 *
 */
public class GlobalException extends RuntimeException {

    protected int code;

    public GlobalException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public GlobalException(String msg, int code, Throwable cause) {
        super(msg, cause);
        this.code = code;
    }

    public GlobalException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code ;
    }

    public String toString() {
        return "code:" + code + ",message:" + getLocalizedMessage();
    }
}
