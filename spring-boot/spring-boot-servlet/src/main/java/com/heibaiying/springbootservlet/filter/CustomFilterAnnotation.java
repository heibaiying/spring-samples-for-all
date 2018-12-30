package com.heibaiying.springbootservlet.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * @author : heibaiying
 * @description : 自定义过滤器
 */

@WebFilter(urlPatterns = "/servletAnn")
public class CustomFilterAnnotation implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(request,response);
        response.getWriter().append(" CustomFilter Annotation");
    }

    @Override
    public void destroy() {

    }
}
