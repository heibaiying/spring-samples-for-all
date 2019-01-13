<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>主页面</title>
</head>
<body>
    <h5>服务器:<%=request.getServerName()+":"+request.getServerPort()%></h5>
    <h5>登录用户: ${sessionScope.USER.username} </h5>
    <h5>用户编号: ${sessionScope.USER.userId} </h5>
</body>
</html>