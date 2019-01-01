<!doctype html>
<html lang="en">
<head>
    <title>产品详情</title>
</head>
<body>
<ul>
    <li>产品名称:${product.name}</li>
    <li>产品序列号:${product.id}</li>
    <li>是否贵重品:${product.isPrecious?string('是','否')}</li>
    <li>生产日期: ${product.dateInProduced?string("yyyy-MM-dd HH:mm:ss")}</li>
    <li>产品价格:${product.price}</li>
</ul>
</body>
</html>
