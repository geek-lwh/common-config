package com.aha.tech.filter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * @Author: luweihong
 * @Date: 2019/11/14
 */
public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream buffer;//输出到byte array
    private ServletOutputStream out;
    private PrintWriter writer;

    public ResponseWrapper(HttpServletResponse resp) throws IOException {
        super(resp);
        buffer = new ByteArrayOutputStream();// 真正存储数据的流
        out = new WapperedOutputStream(buffer);
        writer = new PrintWriter(new OutputStreamWriter(buffer, "utf-8"));
    }

    /** 重载父类获取outputstream的方法 */
    @Override
    public ServletOutputStream getOutputStream() {
        return out;
    }

    /** 重载父类获取writer的方法 */
    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    /** 重载父类获取flushBuffer的方法 */
    @Override
    public void flushBuffer() throws IOException {
        if (out != null) {
            out.flush();
        }
        if (writer != null) {
            writer.flush();
        }
    }

    @Override
    public void reset() {
        buffer.reset();
    }

    public byte[] getResponseData() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /** 内部类，对ServletOutputStream进行包装 */
    private class WapperedOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream bos;

        public WapperedOutputStream(ByteArrayOutputStream stream) {
            bos = stream;
        }

        @Override
        public void write(int b) {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) {
            bos.write(b, 0, b.length);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
        }
    }
}