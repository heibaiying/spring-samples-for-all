<!doctype html>
<html lang="en">
<head>
    <title>产品列表</title>
</head>
<body>
<h3>产品列表:点击查看详情</h3>
<ul>
    <#list products as product>
       <li>
           <a href="sell/product/${product.id}">${product.name}</a>
       </li>
    </#list>
</ul>
</body>
</html>
