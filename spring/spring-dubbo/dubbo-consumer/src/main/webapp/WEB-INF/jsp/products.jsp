<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>产品列表</title>
</head>
<body>
<h3>产品列表:点击查看详情</h3>
<ul>
    <c:forEach items="${products}" var="product">
       <li>
           <a href="sell/product/${product.id}">${product.name}</a>
       </li>
    </c:forEach>
</ul>
</body>
</html>
