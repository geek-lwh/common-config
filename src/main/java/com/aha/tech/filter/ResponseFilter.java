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

import static com.aha.tech.constant.HeaderConstant.*;

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
        String connection = request.getHeader(CONNECTION);
        if (StringUtils.isNotBlank(connection) && connection.toLowerCase().equals(HTTP_HEADER_CONNECTION_VALUE)) {
            String keepAlive = request.getHeader(HTTP_HEADER_KEEP_ALIVE_KEY);
            if (StringUtils.isNotBlank(keepAlive)) {
                response.setHeader(HTTP_HEADER_KEEP_ALIVE_KEY, String.format("timeout=%s, max=50", keepAlive));
            }
//            else{
//                response.setHeader(HTTP_HEADER_KEEP_ALIVE_KEY, String.format("timeout=5, max=50", keepAlive));
//            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }

}
