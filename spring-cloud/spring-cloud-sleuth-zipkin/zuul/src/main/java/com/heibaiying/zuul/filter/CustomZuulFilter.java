package com.heibaiying.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.omg.CORBA.Request;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : heibaiying
 * @description : 自定义filter过滤器
 */

@Component
public class CustomZuulFilter extends ZuulFilter {

    /**
     * 返回过滤器的类型
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    /**
     * 返回过滤器的优先级顺序
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 从此方法返回“true”意味着应该调用下面的 run（）方法
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * ZuulFilter的核心校验方法
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        HttpServletRequest request = currentContext.getRequest();
        String code = (String)request.getSession().getAttribute("code");
        if (StringUtils.isEmpty(code)){
            // 设置值为false 不将请求转发到对应的服务上
            currentContext.setSendZuulResponse(false);
            // 设置返回的状态码
            currentContext.setResponseStatusCode(HttpStatus.NON_AUTHORITATIVE_INFORMATION.value());
            HttpServletResponse response = currentContext.getResponse();
            try {
                // 跳转到登录页面
                response.sendRedirect("/index");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
