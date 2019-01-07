package com.heibaiying.webconfig;

import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

/**
 * @author : heibaiying
 * @description : 编码过滤器 防止乱码
 */
@WebFilter(filterName = "characterEncodingFilter", urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "encoding", value = "UTF-8"),
                @WebInitParam(name = "forceEncoding", value = "true")
        }
)
public class characterEncodingFilter extends CharacterEncodingFilter {

}
