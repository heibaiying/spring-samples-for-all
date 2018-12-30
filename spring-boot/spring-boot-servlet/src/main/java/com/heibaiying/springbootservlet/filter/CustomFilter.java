package com.heibaiying.springbootservlet.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author : heibaiying
 * @description : 自定义过滤器
 */

public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute("filterParam","我是filter传递的参数");
        chain.doFilter(request,response);
        response.getWriter().append(" CustomFilter ");
    }

    @Override
    public void destroy() {

    }
}
