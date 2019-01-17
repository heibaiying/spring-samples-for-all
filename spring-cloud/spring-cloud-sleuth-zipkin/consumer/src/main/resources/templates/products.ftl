<!doctype html>
<html lang="en">
<head>
    <title>产品列表</title>
</head>
<body>
<h3>产品列表:点击查看详情</h3>
<form action="/sell/product" method="post">
    <input type="text" name="productName">
    <input type="submit" value="新增产品">
</form>
<ul>
    <#if (products?size>0) >
        <#list products as product>
            <li>
                <a href="/consumer/sell/product/${product.id}">${product.name}</a>
            </li>
        </#list>
    <#else>
        <h4 style="color: red">当前排队人数过多，请之后再购买！</h4>
    </#if>
</ul>
</body>
</html>
