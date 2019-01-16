package com.heibaiying.zuul.config;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author : heibaiying
 * @description : zuul 的熔断器
 */
@Component
public class CustomZuulFallbackProvider implements FallbackProvider {

    /*
     * 定义熔断将用于哪些路由的服务
     */
    @Override
    public String getRoute() {
        return "consumer";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return new ClientHttpResponse() {

            /**
             * 返回响应的HTTP状态代码
             */
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.SERVICE_UNAVAILABLE;
            }

            /**
             * 返回HTTP状态代码
             */
            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.SERVICE_UNAVAILABLE.value();
            }

            /**
             * 返回响应的HTTP状态文本
             */
            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase();
            }

            @Override
            public void close() {

            }

            /**
             * 将消息正文作为输入流返回
             */
            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("商城崩溃了,请稍后重试！".getBytes());
            }

            /**
             * 将消息正文作为输入流返回
             */
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                return httpHeaders;
            }
        };
    }
}
