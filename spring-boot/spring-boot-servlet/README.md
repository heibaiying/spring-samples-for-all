# Spring Boot 整合 Servlet 

<nav>
<a href="#一项目说明">一、项目说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-结构说明">1.1 结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目依赖">1.2 项目依赖</a><br/>
<a href="#二Spring-注册方式">二、Spring 注册方式</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-组件声明">2.1 组件声明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-组件注册">2.2 组件注册</a><br/>
<a href="#三原生注解方式">三、原生注解方式</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-组件声明">3.1 组件声明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-组件生效">3.2 组件生效</a><br/>
</nav>

## 一、项目说明

### 1.1 结构说明

- 项目提供与 Servlet 整合的两种方式，一种是 Servlet 3.0 原生的注解方式，一种是采用 Spring 注册的方式；
- Servlet、过滤器、监听器分别位于 servlet、filter、listen 下，其中以 Annotation 命名结尾的代表是 Servlet 是以注解方式实现，采用 Spring 注册方式则需要在 ServletConfig 中进行注册；
- 为了说明外置容器对 Servlet 注解的自动发现机制，项目采用外置容器构建，关于 Spring Boot 整合外置容器的详细说明可以参考：[spring-boot-tomcat](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-tomcat) 。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-servlet.png"/> </div>
### 1.2 项目依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <!--排除依赖 使用外部 tomcat 容器启动-->
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    <dependency>
        <!--使用外置容器时候 SpringBootServletInitializer 依赖此包 -->
        <groupId>javax.servlet</groupId>
        <artifactId>servlet-api</artifactId>
        <version>2.5</version>
        <scope>provided</scope>
    </dependency>
    <!--servlet api 注解依赖包-->
    <dependency>
        <groupId>javax.servlet</groupId>
        <artifactId>javax.servlet-api</artifactId>
    </dependency>
</dependencies>
```

## 二、Spring 注册方式

### 2.1 组件声明

声明过滤器、监听器和 Servlet：

```java
/**
 * @description : 自定义过滤器
 */
public class CustomFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setAttribute("filterParam","我是 filter 传递的参数");
        chain.doFilter(request,response);
        response.getWriter().append(" CustomFilter ");
    }

    @Override
    public void destroy() {

    }
}
```

```java
/**
 * @description : 自定义监听器
 */
public class CustomListen implements ServletContextListener {

    //Web 应用程序初始化过程正在启动的通知
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("容器初始化启动");
    }



    /* 通知 servlet 上下文即将关闭
     * 这个地方如果我们使用的是 spring boot 内置的容器 是监听不到销毁过程，所以我们使用了外置 tomcat 容器
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("容器即将销毁");
    }
}
```

```java
/**
 * @description : 自定义 servlet
 */
public class CustomServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("doGet 执行：" + req.getAttribute("filterParam"));
        resp.getWriter().append("CustomServlet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
```

### 2.2 组件注册

```java
@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean registrationBean() {
        return new ServletRegistrationBean<HttpServlet>(new CustomServlet(), "/servlet");
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean<Filter>();
        bean.setFilter(new CustomFilter());
        bean.addUrlPatterns("/servlet");
        return bean;
    }

    @Bean
    public ServletListenerRegistrationBean listenerRegistrationBean() {
        return new ServletListenerRegistrationBean<ServletContextListener>(new CustomListen());
    }

}
```

## 三、原生注解方式

### 3.1 组件声明

新建过滤器、监听器和 servlet,分别使用 @WebFilter、@WebListener、@WebServlet 注解进行声明：

```java
/**
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
```

```java
/**
 * @description :自定义监听器
 */
@WebListener
public class CustomListenAnnotation implements ServletContextListener {

    //Web 应用程序初始化过程正在启动的通知
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("容器初始化启动 Annotation");
    }



    /* 通知 servlet 上下文即将关闭
     * 这个地方如果我们使用的是 spring boot 内置的容器 是监听不到销毁过程，所以我们使用了外置 tomcat 容器
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("容器即将销毁 Annotation");
    }
}
```

```java
/**
 * @description : 自定义 servlet
 */
@WebServlet(urlPatterns = "/servletAnn")
public class CustomServletAnnotation extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().append("CustomServlet Annotation");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
```

### 3.2 组件生效

想要让使用 Servlet 3.0 原生注解方式声明的组件生效，有以下两种方式：

- 如果是内置容器，需要在启动类上添加 @ServletComponentScan ，指定扫描的包目录；
- 如果是外置容器，不需要进行任何配置，依靠容器内建的 Discovery 机制自动发现，需要说明的是这里的容器必须支持 Servlet 3.0（ Tomcat 从 7.0 版本开始支持 Servlet3.0）。