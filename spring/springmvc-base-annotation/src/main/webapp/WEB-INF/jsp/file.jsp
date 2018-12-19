<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>文件上传</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/file.css">
</head>
<body>

    <form action="${pageContext.request.contextPath }/upFile" method="post" enctype="multipart/form-data">
        请选择上传文件：<input name="file" type="file"><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath }/upFiles" method="post" enctype="multipart/form-data">
        请选择上传文件(多选)：<input name="file" type="file" multiple><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath }/upFiles2" method="post" enctype="multipart/form-data">
        请选择上传文件1：<input name="file1" type="file"><br>
        请选择上传文件2：<input name="file2" type="file"><br>
        文件内容额外备注: <input name="extendParam" type="text"><br>
        <input type="submit" value="点击上传文件">
    </form>

    <form action="${pageContext.request.contextPath }/upFileForDownload" method="post" enctype="multipart/form-data">
        上传并下载：<input name="file" type="file"><br>
        <input type="submit" value="点击上传文件">
    </form>

</body>
</html>
