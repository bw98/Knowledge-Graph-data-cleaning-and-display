<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
    <style type="text/css">
        input {
            width: 180px;
            height: 30px;
        }
    </style>
</head>
<body>

<div style="margin-left: 30%; width: 560px">
    <form action="/ShowDemo/LoginServlet" method="post" align="center">
        <input type="hidden" name="method" value="login">
        <p style="text-align: center; color: #0284FF; font-size: 40px; font-weight: bold">
            登录界面
        </p>
        <div style="height: 40px">
            账号:<input type="text" id="username" name="userName" value="" placeholder="输入你的账号名">
        </div>
        <div style="height: 40px">
            密码:<input type="password" id="psword" name="passWord" value="" placeholder="输入你想设置的密码">
        </div>
        <input type="submit" value="立即登录"
               style="width: 400px; height: 40px; background: #0284FF; color: whitesmoke; font-size: 18px"
        >
    </form>
</div>

</body>
</html>
