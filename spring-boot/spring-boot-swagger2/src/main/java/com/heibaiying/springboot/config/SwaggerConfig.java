package com.heibaiying.springboot.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static com.google.common.base.Predicates.not;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * @author : heibaiying
 * @description :  Swagger 配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.enable}")
    private boolean swaggerEnable;

    /***
     * 配置swagger
     * 开发和测试环境下可以开启swagger辅助进行调试,而生产环境下可以关闭或者进行相应的权限控制，防止接口信息泄露
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(swaggerEnable)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.heibaiying.springboot.controller"))
                .paths(PathSelectors.any())
                .paths(doFilteringRules())
                .build();
    }

    /***
     * 接口文档的描述信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("spring boot swagger2 用例")
                .description("描述")
                .licenseUrl("https://mit-license.org/")
                .version("1.0")
                .build();
    }

    /**
     * 可以使用正则定义url过滤规则
     */
    private Predicate<String> doFilteringRules() {
        return not(
                regex("/ignore/*")
        );
    }
}
