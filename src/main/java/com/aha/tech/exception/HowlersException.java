package com.aha.tech.exception;


import com.aha.tech.constant.SystemEnvConstant;
import com.aha.tech.util.RepsonseCodeUtil;

/**
 * 低级错误
 * 抛出warn级别
 */
public class HowlersException extends RuntimeException implements GlobalException {

    private int code;

    private String message;

    public HowlersException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public Integer code() {
        Integer prefix = RepsonseCodeUtil.getApplictionPrefix(SystemEnvConstant.APPLICATION_NAME);
        // 错误码是6位的
        return prefix * 10000 + this.code();
    }

    @Override
    public String message() {
        return this.message;
    }
}
