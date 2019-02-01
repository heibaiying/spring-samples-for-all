# spring websocket（xml配置方式）
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二spring-websocket">二、spring websocket</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-创建消息处理类继承自TextWebSocketHandler">2.1 创建消息处理类，继承自TextWebSocketHandler</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-创建websocket-握手拦截器如果没有权限拦截等需求这一步不是必须的">2.2 创建websocket 握手拦截器（如果没有权限拦截等需求，这一步不是必须的）</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-配置websocket">2.3 配置websocket</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#24-前端-websocket-的实现">2.4 前端 websocket 的实现</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#25-简单登录的实现">2.5 简单登录的实现</a><br/>
## 正文<br/>


## 一、说明

### 1.1 项目结构说明

1. 项目模拟一个简单的群聊功能，为区分不同的聊天客户端，登录时候将临时用户名存储在session当中；
2. CustomHander为消息的自定义处理器；
3. CustomHandershakerInterceptor为自定义的 websocket 的握手拦截器；
4. 项目以web的方式构建。

<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-websocket.png"/> </div>



### 1.2 依赖说明

除了基本的spring 依赖外，还需要导入webSocket的依赖包

```xml
 <!--spring webSocket 的依赖包 -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-websocket</artifactId>
    <version>5.1.3.RELEASE</version>
</dependency>
```



## 二、spring websocket

#### 2.1 创建消息处理类，继承自TextWebSocketHandler

```java
/**
 * @author : heibaiying
 * @description : 自定义消息处理类
 */
public class CustomHandler extends TextWebSocketHandler {

    private Map<String, WebSocketSession> nameAndSession = new ConcurrentHashMap<>();

    // 建立连接时候触发
    @Override
    public void afterConnectionEstablished(WebSocketSession session)  {
        String username = getNameFromSession(session);
        nameAndSession.putIfAbsent(username, session);
    }


    // 关闭连接时候触发
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = getNameFromSession(session);
        nameAndSession.remove(username);
    }

    // 处理消息
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 防止中文乱码
        String msg = URLDecoder.decode(message.getPayload(), "utf-8");
        String username = getNameFromSession(session);
        // 简单模拟群发消息
        TextMessage reply = new TextMessage(username + " : " + msg);
        nameAndSession.forEach((s, webSocketSession)
                -> {
            try {
                webSocketSession.sendMessage(reply);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private String getNameFromSession(WebSocketSession session) {
        Map<String, Object> attributes = session.getAttributes();
        return (String) attributes.get(Constant.USER_NAME);
    }
}

```

#### 2.2 创建websocket 握手拦截器（如果没有权限拦截等需求，这一步不是必须的）

```java
/**
 * @author : heibaiying
 * @description : 可以按照需求实现权限拦截等功能
 */
public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        InetAddress address = remoteAddress.getAddress();
        System.out.println(address);
        /*
         * 最后需要要显示调用父类方法，父类的beforeHandshake方法
         * 把ServerHttpRequest 中session中对应的值拷贝到WebSocketSession中。
         * 如果我们没有实现这个方法，我们在最后的handler处理中 是拿不到 session中的值
         * 作为测试 可以注释掉下面这一行 可以发现自定义处理器中session的username总是为空
         */
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
}
```

#### 2.3 配置websocket

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:websocket="http://www.springframework.org/schema/websocket"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context-4.1.xsd
        http://www.springframework.org/schema/mvc
        http://www.springframework.org/schema/mvc/spring-mvc-4.1.xsd
        http://www.springframework.org/schema/websocket
        http://www.springframework.org/schema/websocket/spring-websocket.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.heibaiying.*"/>

    <!--使用默认的Servlet来响应静态文件 -->
    <mvc:default-servlet-handler/>

    <!-- 开启注解驱动 -->
    <mvc:annotation-driven/>

    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver"
          id="internalResourceViewResolver">
        <!-- 前缀 -->
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <!-- 后缀 -->
        <property name="suffix" value=".jsp"/>
    </bean>

    <!--配置webSocket-->
    <bean id="customHandler" class="com.heibaiying.websocket.CustomHandler"/>
    <websocket:handlers>
        <!--指定webSocket 地址-->
        <websocket:mapping path="/socket" handler="customHandler"/>
        <!--webSocket握手-->
        <websocket:handshake-interceptors>
            <bean class="com.heibaiying.websocket.CustomHandshakeInterceptor"/>
        </websocket:handshake-interceptors>
    </websocket:handlers>

</beans>
```

#### 2.4 前端 websocket 的实现

```jsp
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>${sessionScope.get("username")}您好！欢迎进入群聊大厅！</title>
</head>
<body>
<input id="message" type="text">
<button id="btn">发送消息</button>
<div id="show">

</div>
<script>
    let btn = document.getElementById("btn");
    let message = document.getElementById("message");
    let show = document.getElementById("show");
    let ws = new WebSocket("ws://localhost:8080/socket");
    ws.onmessage = function (evt) {
        let node = document.createElement("div");
        node.innerHTML = "<h5>" + evt.data + "</h5>";
        show.appendChild(node);
    };
    btn.addEventListener("click", function () {
        let data = message.value;
        console.log(data);
        if (data) {
            ws.send(encodeURI(data));
        } else {
            alert("请输入消息后发送");
        }
        message.value = "";
    });
    // 关闭页面时候关闭ws
    window.addEventListener("beforeunload", function(event) {
        ws.close();
    });
</script>
</body>
</html>

```

#### 2.5 简单登录的实现

```java
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<form action="${pageContext.request.contextPath}/login" method="post">
    <input name="username" type="text">
    <button id="btn">输入临时用户名后登录！</button>
</form>
</body>
</html>
```

```java
@Controller
public class LoginController {

    @PostMapping("login")
    public String login(String username, HttpSession session){
        session.setAttribute(Constant.USER_NAME,username);
        return "chat";
    }
}
```

