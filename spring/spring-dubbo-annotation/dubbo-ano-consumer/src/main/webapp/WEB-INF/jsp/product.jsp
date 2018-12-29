<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>产品详情</title>
</head>
<body>
<ul>
    <li>产品名称:${product.name}</li>
    <li>产品序列号:${product.id}</li>
    <li>是否贵重品:${product.isPrecious?"是":"否"}</li>
    <li>生产日期:<fmt:formatDate value="${product.dateInProduced}" pattern="yyyy年MM月dd日HH点mm分ss秒"/></li>
    <li>产品价格:${product.price}</li>
</ul>
</body>
</html>
