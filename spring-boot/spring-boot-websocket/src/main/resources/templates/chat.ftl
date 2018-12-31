<!doctype html>
<html lang="en">
<head>
    <title>${Session["username"]}您好！欢迎进入群聊大厅！</title>
</head>
<body>
    <h5>${Session["username"]}您好！欢迎进入群聊大厅！</h5>
    <input id="message" type="text">
    <button id="btn">发送消息</button>
    <div id="show">

    </div>
<script>
    let btn = document.getElementById("btn");
    let message = document.getElementById("message");
    let show = document.getElementById("show");
    let ws = new WebSocket("ws://localhost:8080/socket/${Session["username"]}");
    ws.onmessage = function (evt) {
        let node = document.createElement("div");
        node.innerHTML = "<h5>" + evt.data + "</h5>";
        show.appendChild(node);
    };
    btn.addEventListener("click", function () {
        let data = message.value;
        console.log(data);
        if (data) {
            ws.send(encodeURI(data));
        } else {
            alert("请输入消息后发送");
        }
        message.value = "";
    });
    // 关闭页面时候关闭ws
    window.addEventListener("beforeunload", function (event) {
        ws.close();
    });
</script>
</body>
</html>
