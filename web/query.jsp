<%--
  Created by IntelliJ IDEA.
  User: 蒙健乐
  Date: 2018/7/26
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Query</title>
    <script src="js/jquery.min.js"></script>
    <script src="js/jquery.form.js"></script>
    <link href="css/dataPreprocess.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript">
        function query() {
            var time = $("#time").val();
            var location = $("#location").val();
            var keys = $("#keys").val();
            $.ajax({
                url: "servlet/QueryServlet",
                data: {time:time,location:location,keys: keys},
                async: true,   //是否为异步请求
                cache: false,  //是否缓存结果
                type: "POST", //请求方式为POST
                dataType: "text",  //服务器返回的数据是什么类型
                success:function(result){  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                    if(result){
                        var obj = document.getElementById("result");
                        obj.scrollTop = obj.scrollHeight; // good
                        obj.innerHTML = result;
                    }
                }
            })
        }
    </script>
</head>
<body>
<header>
    <span>IRTree Build</span>
</header>
<aside >
    <ul class="menu">
        <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" >Back Home</a></li>
    </ul>
</aside>
<section id="main">
    <section id="con">
        <div class="content">
            <div id="params1" style="display: block">
                <label>时间（可缺省）:&nbsp;&nbsp;<input id="time" type="text" name="时间（可缺省）"></label>
            </div>
        </div>
        <div class="content">
            <div id="params2" style="display: block">
                <label>地点（必填）:&nbsp;&nbsp;<input id="location" type="text" name="地点（必填）"></label>
            </div>
        </div>
        <div class="content">
            <div id="params3" style="display: block">
                <label>"关键字（必填）":&nbsp;&nbsp;<input id="keys" type="text" name="关键字（必填）"></label>
            </div>
        </div>
        <div class="content">
            <label><input id="build" class="green_button" type="button" value="Query" style="height:40px;width:80px;"
                          onclick="query();"/> </label>
        </div>
        <div class="content">
            <textarea id="result" rows="19" cols="52" readonly></textarea>
        </div>
    </section>
</section>
</body>
</html>
