<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <title>Home</title>
    <!-- Animate.css -->
    <link rel="stylesheet" href="css/animate.css">
    <!-- Bootstrap  -->
    <link rel="stylesheet" href="css/bootstrap.css">
    <!-- Theme style  -->
    <link rel="stylesheet" href="css/style.css">

    <!--js-->
    <!-- Modernizr JS -->
    <script src="js/modernizr-2.6.2.min.js"></script>
    <!-- FOR IE9 below -->
    <!--[if lt IE 9]>
    <script src="js/respond.min.js"></script>
    <![endif]-->
    <!-- jQuery -->
    <script src="js/jquery-3.3.1.min.js"></script>
    <!-- Waypoints -->
    <script src="js/jquery.waypoints.min.js"></script>
    <!-- Carousel -->
    <script src="js/owl.carousel.min.js"></script>
    <!-- Main -->
    <script src="js/main.js"></script>

</head>
<body>

<div class="gtco-loader"></div>

<div id="page">


    <div class="page-inner">


        <header id="gtco-header" class="gtco-cover" role="banner" style="background-image: url(images/img_4.jpg)">
            <div class="overlay"></div>
            <div class="gtco-container">
                <div class="row">
                    <div class="col-md-12 col-md-offset-0 text-left">
                        <div class="row row-mt-15em">
                            <div class="col-md-7 mt-text animate-box" data-animate-effect="fadeInUp">
                                <span class="intro-text-small">西邮文本挖掘兴趣小组</span>
                                <h1>基于Neo4j的知识图谱展示项目</h1>
                            </div>
                            <div class="col-md-4 col-md-push-1 animate-box" data-animate-effect="fadeInRight">
                                <div class="form-wrap">
                                    <div class="tab">
                                        <ul class="tab-menu">
                                            <li class="gtco-second"><a href="#" data-tab="signup">注册</a></li>
                                            <li class="active gtco-first"><a href="#" data-tab="login">登录</a></li>
                                        </ul>
                                        <div class="tab-content">
                                            <div class="tab-content-inner" data-content="signup">
                                                <form action="/ShowDemo/LoginServlet" method="post">
                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <label for="username">用户名</label>
                                                            <input type="text" class="form-control">
                                                        </div>
                                                    </div>
                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <label for="password">密&nbsp;&nbsp;码</label>
                                                            <input type="password" class="form-control">
                                                        </div>
                                                    </div>
                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <label for="password2">重复密码</label>
                                                            <input type="password" class="form-control" id="password2">
                                                        </div>
                                                    </div>

                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <input type="submit" class="btn btn-primary" value="注册">
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>

                                            <div class="tab-content-inner active" data-content="login">
                                                <form action="/ShowDemo/LoginServlet" method="post">
                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <label for="username">用户名</label>
                                                            <input type="text" class="form-control username"
                                                                   id="username" name="username"><br>
                                                            <text class="error1" style="color: red"></text>
                                                        </div>
                                                    </div>
                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <label for="password">密&nbsp;&nbsp;码</label>
                                                            <input type="password" class="form-control password"
                                                                   id="password" name="password"><br>
                                                            <text class="error2" style="color: red"></text>
                                                        </div>
                                                    </div>

                                                    <div class="row form-group">
                                                        <div class="col-md-12">
                                                            <input type="submit" class="btn btn-primary submit"
                                                                   value="登录">
                                                        </div>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    </div>

</div>

<div class="gototop js-top">
    <a href="#" class="js-gotop"><i class="icon-arrow-up"></i></a>
</div>

</body>
</html>