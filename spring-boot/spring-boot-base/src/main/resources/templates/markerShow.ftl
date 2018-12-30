<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>freemarker模板引擎</title>
</head>
<body>
    <ul>
        <#list programmers as programmer>
           <li>姓名: ${programmer.name} 年龄: ${programmer.age}</li>
        </#list>
    </ul>
</body>
</html>