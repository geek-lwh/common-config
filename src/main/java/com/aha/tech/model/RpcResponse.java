package com.aha.tech.model;

import com.aha.tech.exception.BaseException;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 说明:响应对象
 *
 */
public class RpcResponse<T> {

    private Integer code = 0;// 0正常
    private String message = "成功";

    private T data;

    public RpcResponse() {
        this(0, "成功");
    }

    public RpcResponse(Integer code, String message) {
        this(code, message, null);
    }

    public RpcResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public RpcResponse(T t) {
        this();
        this.data = t;
    }

    /**
     * 默认hystrix fallback 返回实体
     * @return
     */
    public static RpcResponse defaultHystrixFallbackResponse(){
        return new RpcResponse( 1,"接口调用异常,执行默认降级策略");
    }

    /**
     * code : 1
     * msg : operation failure
     * @return
     */
    public static RpcResponse defaultFailureResponse(){
        return new RpcResponse(1,"操作异常");
    }

    public RpcResponse(int code,BaseException e) {
        this(code, e.getMessage());
    }

    public RpcResponse(int code ,Exception e) {
        this(code, e.getMessage());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }
}
