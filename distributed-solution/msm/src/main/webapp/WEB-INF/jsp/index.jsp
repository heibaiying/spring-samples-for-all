<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录页面</title>
</head>
<body>
<h5>服务器:<%=request.getServerName()+":"+request.getServerPort()%></h5>
<form action="${pageContext.request.contextPath}/login" method="post">
     用户：<input type="text" name="username"><br/>
     密码：<input type="password" name="password"><br/>
    <button type="submit">登录</button>
</form>
</body>
</html>