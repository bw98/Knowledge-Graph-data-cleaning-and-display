<%--
  Created by IntelliJ IDEA.
  User: laiweifeng
  Date: 2019/6/2
  Time: 22:11
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <title>主界面</title>
    <link rel="stylesheet" href="layui/css/layui.css">
    <link rel="stylesheet" href="css/myStyle.css">

    <!--import d3 version 5-->
    <script type="text/javascript" src="js/d3.min.js"></script>
    <!--import jquery3.3.1-->
    <script type="text/javascript" src="js/jquery-3.3.1.min.js"></script>

    <link rel="stylesheet" href="css/jquery-ui.min.css">
    <script src="js/jquery-ui.min.js"></script>
</head>
<body class="layui-layout-body">



<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">知识图谱搜索系统</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left">
                <li class="layui-nav-item">
                    <!-- 输入查询内容-->
                    <!--这里不可以为空-->
                    <form class="myform" action="/ShowDemo/DisplayServlet" method="post" align="center">
                    <input type="text" id="organization_name" placeholder="ABB中国研究院" autocomplete="off" class="layui-input input">
                    </form>
                </li>
                <li class="layui-nav-item">
                    <!--查询-->
                    <button class="layui-btn layui-btn-radius checkButton" onclick="checkButton()">查询</button>
                    <%--<input class="layui-btn layui-btn-radius checkButton" onclick="checkButton()">查询</input>--%>
                    <%--</li>--%>
                    <span>&nbsp;&nbsp;</span>
                <li class="layui-nav-item">
                    <!--清空-->
                    <button class="layui-btn layui-btn-radius clearButton" onclick="clearButton()">清空</button>
                    <%--<button class="layui-btn layui-btn-radius clearButton" onclick="clearButton()">清空</button>--%>
                </li>
        </ul>
        <ul class="layui-nav layui-layout-right">

            <li class="layui-nav-item">
                是否隐藏后继节点：
                <input type="radio" name="radio" value="yes">是&nbsp;&nbsp;
                <input type="radio" name="radio" value="no">否
            </li>

            <li class="layui-nav-item">
                <a href="javascript:;">
                    <img src="http://t.cn/RCzsdCq" class="layui-nav-img">
                    赖伟峰
                </a>
                <dl class="layui-nav-child">
                    <dd><a href="showUserInfoConfig.jsp">基本资料</a></dd>
                    <dd><a href="">安全设置</a></dd>
                </dl>
            </li>
            <li class="layui-nav-item"><a href="">退出</a></li>
        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree"  lay-filter="test">
                <li class="layui-nav-item layui-nav-itemed">
                    <a class="" href="javascript:;">添加</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" data-method="addNode" class="addNode"><span>&nbsp;&nbsp;</span>添加节点</a></dd>
                        <dd><a href="javascript:;" data-method="addRel" class="addRel"><span>&nbsp;&nbsp;</span>添加关系</a></dd>
                        <dd>
                            <a href="javascript:;"><span>&nbsp;&nbsp;</span>上传csv文件</a>
                            <dl class="layui-nav-child">
                                <dd><a href="javascript:;"><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>上传添加节点.csv</a></dd>
                                <dd><a href="javascript:;"><span>&nbsp;&nbsp;&nbsp;&nbsp;</span>上传添加关系.csv</a></dd>
                            </dl>
                        </dd>
                    </dl>
                </li>
            </ul>
        </div>
    </div>
    <div class="layui-body">

        <svg class=".svg" width="100%" height="100%"></svg>

    </div>

    <div class="layui-footer">

    </div>
</div>
<script src="layui/layui.js"></script>


<script>
    //JavaScript代码区域
    layui.use(['element','layer'], function(){
        var element = layui.element;
        var layer = layui.layer;

        var $ = layui.jquery;

        var active = {

            addNode: function(){
                //示范一个公告层
                layer.open({
                    type: 1
                    ,title: false //不显示标题栏
                    ,closeBtn: false
                    ,area: '300px;'
                    ,shade: 0.8
                    ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
                    ,btn: ['取消']
                    ,btnAlign: 'c'
                    ,moveType: 1 //拖拽模式，0或者1
                    ,content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">' +
                        '<form action="AddNodeController1?flag=addNode" method="post">'+
                        '节点类型：<input type="text" name="nodeType">'+
                        '<input type="submit" value="提交" >'+
                        '</form>'+
                        '</div>'
                });
            },

            addRel: function(){
                //示范一个公告层
                layer.open({
                    type: 1
                    ,title: false //不显示标题栏
                    ,closeBtn: false
                    ,area: '300px;'
                    ,shade: 0.8
                    ,id: 'LAY_layuipro' //设定一个id，防止重复弹出
                    ,btn: ['取消']
                    ,btnAlign: 'c'
                    ,moveType: 1 //拖拽模式，0或者1
                    ,content: '<div style="padding: 50px; line-height: 22px; background-color: #393D49; color: #fff; font-weight: 300;">' +
                        '<form action="AddNodeController1?flag=addRel" method="post">'+
                        '前驱节点类型：<input type="text" name="fromNodeType"> <br>'+
                        ' 关 系 类 型：<input type="text" name="relType"> <br>'+
                        '后继节点类型：<input type="text" name="toNodeType">'+
                        '<input type="submit" value="提交" >'+
                        '</form>'+
                        '</div>'
                });
            }
        };

        $('.addNode').on('click', function(){
            var othis = $(this), method = othis.data('method');
            active[method] ? active[method].call(this, othis) : '';
        });
        $('.addRel').on('click', function(){
            var othis = $(this), method = othis.data('method');
            active[method] ? active[method].call(this, othis) : '';
        });
    });

</script>

<script type="text/javascript">
    function checkButton(){
        //注意！！不能同时提交两个表单，否则第一个表单中的input的值在servlet界面获取不到
        //如果是多个选择框而且想这样写，是不行的
        //$(".myform").submit();
        //$(".selectForm").submit();
        // var selectItem1 = $('.selectForm1 option:selected').val();
        // var selectItem2 = $('.selectForm2 option:selected').val();
        var selectItem1 = "organization";  // 复选框1中对应的是结点类型，因为我只需要做organization，所以就不需要复选框，默认结点类型是organization
        var selectItem2 = "";  // 复选框2中也是对应的是子结点类型，如果复选框1和2都有值，则将2的值作为结点类型进行查询，目前我暂时不需要
        var keyword = $("#organization_name").val(); // keyword 是结点的name属性
        var isHide = $('input:radio:checked').val();

        new display(selectItem1,selectItem2,keyword,"first",isHide);
    }

    function display(parameter1,parameter2,parameter3,parameter4,parameter5){
        //这一行代码很重要，否则！查询的时候，前面查询的结果还在
        $("svg").html("");
        d3.json("/ShowDemo/DisplayServlet?selectItem1="+parameter1+"&selectItem2="+parameter2+"&keyword="+parameter3+"&times="+parameter4+"&isHide="+parameter5).then(function(dataJson){
            //GroupExplorer constructing function
            //this is one way to create a javascript object
            function GroupExplorer(data){
                //create an object-include some data
                //this is an another way to create a javascript object

                var defaultConfig = {
                    data:{"nodes":[],"links":[]},//critical data set
                    windowWidth:window.innerWidth,
                    windowHeight:window.innerHeight,
                    defaultLinkDistance:200
                }

                //because the initial "data" is null,
                //so you need use jquery syntax "extend" to merge the json data
                //below,merge "data" into "defaultWindow"
                //"true" mean do not cover
                //details see jquery API document
                $.extend(true,defaultConfig,data);

                //so now we get the json file that we need
                //now let`s begin,transform json file to force graph data
                //but first ,we need to get "svg"

                var svg = d3.select("svg");
                var svgWidth = svg.attr("width");
                var svgHeight = svg.attr("height");
                /*svg.attr("width",defaultConfig.windowWidth);
                svg.attr("height",defaultConfig.windowHeight);*/

                defaultConfig.data.links.forEach(function(e){
                    if(typeof e.source=="number"&&typeof e.target=="number"){
                        var sourceNode = defaultConfig.data.nodes.filter(function(n){
                            return n.id === e.source;
                        })[0];
                        var targetNode = defaultConfig.data.nodes.filter(function(n){
                            return n.id === e.target;
                        })[0];
                        e.source = sourceNode;
                        e.target = targetNode;
                    }
                });

                //create a force graph
                var forceSimulation = d3.forceSimulation()
                    .force("link",d3.forceLink())
                    .force("charge",d3.forceManyBody())
                    .force("center",d3.forceCenter(defaultConfig.windowWidth/2,defaultConfig.windowHeight/2));

                //transform nodes data
                forceSimulation.nodes(defaultConfig.data.nodes)
                    .on("tick",ticked);
                //tranform links data
                forceSimulation.force("link")
                    .links(defaultConfig.data.links)
                    .distance(defaultConfig.defaultLinkDistance);

                console.log(defaultConfig.data.links);


                //define arrow
                svg.append("svg:defs")
                    .append("svg:marker")
                    .attr("id", "marker")
                    .attr('viewBox', '0 -5 10 10')
                    //改变箭头位置
                    .attr("refX", 23)
                    .attr("refY",0)
                    .attr('markerWidth', 10)
                    .attr('markerHeight', 10)
                    .attr('orient', 'auto')
                    .append('svg:path')
                    .attr('d', 'M0,-5L10,0L0,5')
                    .attr("fill","brown");
                //draw links
                var links = svg.append("g")
                    .selectAll("line")
                    .data(defaultConfig.data.links)
                    .enter()
                    .append("line")
                    .attr("title","")
                    .attr("x1",function(n){return n.source.x})
                    .attr("y1",function(n){return n.source.y})
                    .attr("x2",function(n){return n.target.x})
                    .attr("y2",function(n){return n.target.y})
                    .attr("stroke","brown")
                    .attr("stroke-width",2)
                    .attr("marker-end","url(#marker)")
                    .on("mouseover",mouseOverRel);
                //draw links-text
                var links_text = svg.append("g")
                    .selectAll("text")
                    .data(defaultConfig.data.links)
                    .enter()
                    .append("text")
                    .attr("x",function(e){
                        return (e.source.x+e.target.x)/2;
                    })
                    .attr("y",function(e){
                        return (e.source.y+e.target.y)/2;
                    })
                    .attr("font-size",10)
                    .text(function(e){return e.type});
                //draw nodes group = node+node-text
                var nodes_g = svg.append("g")
                    .selectAll("g")
                    .data(defaultConfig.data.nodes)
                    .enter()
                    .append("g")
                    .attr("transform",function(e){
                        return "translate("+e.x+","+e.y+")";
                    })
                    .call(d3.drag()
                        .on("start",started)
                        .on("drag",dragged)
                        .on("end",ended));
                //draw nodes
                nodes_g.append("circle")
                    .attr("title","")
                    .attr("r",30)
                    .attr("fill",function (e) {
                        console.log(e.color);
                        return e.color;
                    })
                    .on("mouseover",mouseOverNode)
                    .on("dblclick",doubleClick);
                //draw node-text
                nodes_g.append("text")
                    .attr("x",-40)
                    .attr("y",40)
                    .attr("font-size",10)
                    .text(function(e){return e.name});

                function started(d){
                    if(!d3.event.active){
                        forceSimulation.alphaTarget(0.8).restart();
                    }
                    d.fx = d.x;
                    d.fy = d.y;
                }
                function dragged(d){
                    d.fx = d3.event.x;
                    d.fy = d3.event.y;
                }
                function ended(d) {
                    if(!d3.event.active){
                        forceSimulation.alphaTarget(0);
                    }
                    d.fx = null;
                    d.fy = null;
                }

                function ticked(){
                    links
                        .attr("x1",function(n){return n.source.x})
                        .attr("y1",function(n){return n.source.y})
                        .attr("x2",function(n){return n.target.x})
                        .attr("y2",function(n){return n.target.y})
                    links_text
                        .attr("x",function(e){
                            return (e.source.x+e.target.x)/2;
                        })
                        .attr("y",function(e){
                            return (e.source.y+e.target.y)/2;
                        })
                    nodes_g
                        .attr("transform",function(e){
                            return "translate("+e.x+","+e.y+")";
                        })
                }

                //节点的监听
                function mouseOverNode(data) {

                    var keys = Object.keys(data);
                    var content = "";

                    for(var i = 0;i<keys.length;i++){
                        var key = keys[i];

                        //筛选key
                        //其中有几个是默认的key，我们不需要，可以根据console后台打印的node数据格式可以看到
                        //id
                        //index
                        //x
                        //y
                        //vx
                        //vy
                        //还有在拖拽的时候会产生新的两个key——fx和fy
                        if(key=="id"||key=="index"||key=="x"||key=="y"||key=="vx"||key=="vy"||key=="fx"||key=="fy")
                            continue;
                        //通过这样获取value
                        var value = data[key];
                        content += key+"："+value+"   ";
                    }

                    $(this).attr("title",content);
                }

                //关系的监听
                function mouseOverRel(data) {

                    var keys = Object.keys(data);
                    var content = "";

                    for(var i = 0;i<keys.length;i++){
                        var key = keys[i];

                        //筛选key
                        //其中有几个是默认的key，我们不需要，可以根据console后台打印的relationship数据格式可以看到
                        //index
                        //source
                        //target

                        //还有在拖拽的时候会产生新的两个key——fx和fy
                        if(key=="index"||key=="source"||key=="target")
                            continue;
                        //通过这样获取value
                        if(key=="type"){
                            content += "关系类型"+"："+data.type+"   ";
                            continue;
                        }
                        var value = data[key];
                        content += key+"："+value+"   ";
                    }

                    $(this).attr("title",content);
                }

                //处理双击事件
                //相当于递归调用
                function doubleClick(data){
                    var nodeName = data.name;
                    var nodeType = data.type;

                    //得到所需要的nodeName,和nodeType
                    //可以调用查询按钮点击事件，即将这次的双击事件看成查询事件，只是在后台调用不同的查询方法
                    var parameter1 = nodeType;
                    var parameter2 = "";
                    var parameter3 = nodeName;
                    var parameter4 = "other";
                    var isHide = $('input:radio:checked').val();
                    console.log(isHide);

                    new display(parameter1,parameter2,parameter3,parameter4,isHide);

                    console.log(nodeType+"----"+nodeName);
                }

            }
            //because in the way of creating a javascript object,
            //you need to use "new" to use it
            new GroupExplorer({data:dataJson});
        })
    }

    function clearButton() {
        $(".input").val("");
    }
</script>

<script>
    layui.use(['form', 'layedit', 'laydate'], function(){
        var form = layui.form
            ,layer = layui.layer
    });
</script>

<script type="text/javascript">
    $(document).tooltip();
</script>

</body>
</html>
