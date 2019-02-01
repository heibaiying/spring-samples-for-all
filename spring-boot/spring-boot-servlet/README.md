# spring boot 整合 servlet 
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-项目依赖">1.2 项目依赖</a><br/>
<a href="#二采用spring-注册方式整合-servlet">二、采用spring 注册方式整合 servlet</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-新建过滤器监听器和servlet">2.1 新建过滤器、监听器和servlet</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-注册过滤器监听器和servlet">2.2 注册过滤器、监听器和servlet</a><br/>
<a href="#三采用注解方式整合-servlet">三、采用注解方式整合 servlet</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#31-新建过滤器监听器和servlet分别使用@WebFilter@WebListener@WebServlet注解标注">3.1 新建过滤器、监听器和servlet,分别使用@WebFilter、@WebListener、@WebServlet注解标注</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#32-使注解生效">3.2 使注解生效</a><br/>
## 正文<br/>


## 一、说明

#### 1.1 项目结构说明

1. 项目提供与servlet整合的两种方式，一种是servlet3.0 原生的注解方式，一种是采用spring 注册的方式；
2. servlet、过滤器、监听器分别位于servlet、filter、listen 下，其中以Annotation命名结尾的代表是servlet注解方式实现，采用spring注册方式则在ServletConfig中进行注册；
3. 为了说明外置容器对servlet注解的自动发现机制，项目采用外置容器构建，关于spring boot 整合外置容器的详细说明可以参考[spring-boot-tomcat](https://github.com/heibaiying/spring-samples-for-all/tree/master/spring-boot/spring-boot-tomcat)

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-boot-servlet.png"/> </div>

#### 1.2 项目依赖

```xml
<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <!--排除依赖 使用外部tomcat容器启动-->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-tomcat</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <!--使用外置容器时候SpringBootServletInitializer 依赖此包 -->
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

## 二、采用spring 注册方式整合 servlet

#### 2.1 新建过滤器、监听器和servlet

```java
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
```

```java
/**
 * @author : heibaiying
 * @description : 自定义监听器
 */
public class CustomListen implements ServletContextListener {

    //Web应用程序初始化过程正在启动的通知
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("容器初始化启动");
    }



    /* 通知servlet上下文即将关闭
     * 这个地方如果我们使用的是spring boot 内置的容器 是监听不到销毁过程，所以我们使用了外置 tomcat 容器
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("容器即将销毁");
    }
}
```

```java
/**
 * @author : heibaiying
 * @description : 自定义servlet
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

#### 2.2 注册过滤器、监听器和servlet

```java
/**
 * @author : heibaiying
 */
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

## 三、采用注解方式整合 servlet

#### 3.1 新建过滤器、监听器和servlet,分别使用@WebFilter、@WebListener、@WebServlet注解标注

```java
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
```

```java
/**
 * @author : heibaiying
 * @description :自定义监听器
 */
@WebListener
public class CustomListenAnnotation implements ServletContextListener {

    //Web应用程序初始化过程正在启动的通知
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("容器初始化启动 Annotation");
    }



    /* 通知servlet上下文即将关闭
     * 这个地方如果我们使用的是spring boot 内置的容器 是监听不到销毁过程，所以我们使用了外置 tomcat 容器
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("容器即将销毁 Annotation");
    }
}
```

```java

/**
 * @author : heibaiying
 * @description : 自定义servlet
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

#### 3.2 使注解生效

1. 如果是内置容器，需要在启动类上添加@ServletComponentScan("com.heibaiying.springbootservlet") ，指定扫描的包目录；
2. 如果是外置容器，不需要进行任何配置，依靠容器内建的discovery机制自动发现，需要说明的是这里的容器必须支持servlet3.0（tomcat从7.0版本开始支持Servlet3.0）。