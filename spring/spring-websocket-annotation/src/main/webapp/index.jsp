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
