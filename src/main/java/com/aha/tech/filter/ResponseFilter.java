package com.aha.tech.filter;

import com.aha.tech.constant.OrderedConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 说明:过滤打印输入输出参数Log
 *
 * @author huangkeqi date:2018年1月19日
 */
@Component
@Order(OrderedConstant.RESPONSE_FILTER)
@WebFilter(filterName = "CrossDomainRequestFilter", urlPatterns = "/*")
public class ResponseFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        // 表明，该资源可以被任意外域访问 Access-Control-Allow-Origin: http://foo.example
        response.setHeader("Access-Control-Allow-Origin", "*");

        // 表明服务器允许客户端使用 POST,PUT,GET,DELETE 发起请求
        response.setHeader("Access-Control-Allow-Methods", "POST,PUT,GET,DELETE");

        // 表明该响应的有效时间为 10 秒
        response.setHeader("Access-Control-Max-Age", "10");

        // 表明服务器允许请求中携带字段 X-PINGOTHER 与 Content-Type x-requested-with
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with,Content-Type");

        String connection = request.getHeader("Connection");
        if (StringUtils.isBlank(connection)) {
            return;
        }

        if (connection.equals("close")) {
            return;
        }

        String keepAlive = request.getHeader("Keep-Alive");
        if (StringUtils.isNotBlank(keepAlive)) {
            String v = "timeout=" + keepAlive + ", max=50";
            response.setHeader("Keep-Alive", v);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
