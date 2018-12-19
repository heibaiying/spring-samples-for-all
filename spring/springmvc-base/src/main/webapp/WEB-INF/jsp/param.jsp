<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Restful</title>
    <script src="${pageContext.request.contextPath}/js/jquery3.3.1.js"></script>
</head>
<body>
<ul>
    <li>姓名：${empty name ? p.name : name}</li>
    <li>年龄：${empty age ? p.age : age}</li>
    <li>薪酬：${empty salary ? p.salary : salary}</li>
    <li>生日：${empty birthday ? p.birthday : birthday}</li>
</ul>
</body>
</html>
