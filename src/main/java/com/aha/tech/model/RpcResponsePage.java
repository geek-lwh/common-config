package com.aha.tech.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 说明:响应对象分页查下的返回
 *
 * @author huangkeqi date:2016年7月2日
 */
public class RpcResponsePage<T> extends RpcResponse<T> {

    private String cursor;// 光标

    public RpcResponsePage() {}


    public RpcResponsePage(String cursor) {
        super();
        this.cursor = cursor;
    }

    public RpcResponsePage(String cursor, T data) {
        super(data);
        this.cursor = cursor;
    }

    public RpcResponsePage(String cursor, Integer code, String message, T data) {
        super(code, message, data);
        this.cursor = cursor;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

}
