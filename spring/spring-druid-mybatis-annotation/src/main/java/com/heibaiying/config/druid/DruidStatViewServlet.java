package com.heibaiying.config.druid;

import com.alibaba.druid.support.http.StatViewServlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

/**
 * @author : heibaiying
 * @description : 配置监控页面用户名密码
 */
@WebServlet(urlPatterns = "/druid/*",
        initParams={
                @WebInitParam(name="resetEnable",value="true"),
                @WebInitParam(name="loginUsername",value="druid"),
                @WebInitParam(name="loginPassword",value="druid")
        })
public class DruidStatViewServlet extends StatViewServlet {
}
