
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html>
  <head>
    <title>Multi-platform Data Crawler</title>
    <link href="css/dataCrawl.css" rel="stylesheet" type="text/css"/>
    <script src="js/jquery.min.js"></script>
    <script src="js/jquery.form.js"></script>
    <script type="text/javascript">

        var timer;
        var isGoing = false;
        var isPause = false;
        var minute = 0;
        var second = 0;

        function isStop(){
            if(isGoing == false && isPause == false) {
                return true;
            }else {
                alert("Please Stop before jump to another page.")
                return false;
            }
        }

        var initParam = {west_long:"-180", south_lat:"-90", east_long:"180", north_lat:"90", tag:"*", location:"37,-122", locationRadius:"100km, 8mi", queryTerm:"cat"};

        function start (){
            if (isGoing == false){
                isGoing == true;
                var time = $("#time").val() * 60;
                var Twitter = document.getElementById("Twitter").checked;
                var Flickr = document.getElementById("Flickr").checked;
                var Tumblr = document.getElementById("Tumblr").checked;
                var YouTube = document.getElementById("YouTube").checked;

                var params = JSON.stringify(initParam);

                if ( time == "")
                {
                    alert("Please fill in the crawl time！");
                }else if(!(Twitter||Flickr||Tumblr||YouTube)){
                    alert("Please Choose the platform！");
                } else{
                    $.ajax({
                        url:"/DataGetServlet",//要请求的服务器url
                        data:{option:"start",time:time,Twitter:Twitter,Flickr:Flickr,Tumblr:Tumblr,YouTube:YouTube, crawlerType:"Location", params:params},  //要向服务器发送的数据
                        async:true,   //是否为异步请求
                        cache:false,  //是否缓存结果
                        type:"POST", //请求方式为POST
                        dataType:"json",   //服务器返回的数据是什么类型
                        success:function(result){  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                            if(result){
                                isGoing = true;
                                if(isPause == true){
                                   time = time - minute * 60;
                                }else{
                                    second = 0;
                                    minute = 0;
                                }
                                query (time);
                            }
                        }
                    });
                }
            }else{
                alert("Not over yet！");
            }
        }

        function query (time){
            var Twitter = document.getElementById("Twitter").checked;
            var Flickr = document.getElementById("Flickr").checked;
            var Tumblr = document.getElementById("Tumblr").checked;
            var YouTube = document.getElementById("YouTube").checked;
             timer = setInterval(function(){
                $.ajax({
                    url:"/StatGetServlet",//要请求的服务器url
                    data:{Twitter:Twitter,Flickr:Flickr,Tumblr:Tumblr,YouTube:YouTube},
                    async:true,   //是否为异步请求
                    cache:false,  //是否缓存结果
                    type:"POST", //请求方式为POST
                    dataType:"text",   //服务器返回的数据是什么类型
                    success:function(result){  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                        if(result){
                            var obj = document.getElementById("data");
                            obj.scrollTop = obj.scrollHeight; // good
                            obj.innerHTML = result;
                        }
                    }
                });

                 second++;
                 if ((minute * 60) + second >= time){
                     alert("Over！");
                     clearInterval(timer);
                     stop();
                 }

                 if (second==60){
                     second=0;minute++;
                 }
                 document.getElementById("timeCost").innerHTML = "Time Cost：&nbsp;&nbsp;" + minute + ":" + second;
                 document.getElementById('pg').value = 100 * ( minute * 60 + second ) / time;

            },1000);
        }

        function pause (){
            var Twitter = document.getElementById("Twitter").checked;
            var Flickr = document.getElementById("Flickr").checked;
            var Tumblr = document.getElementById("Tumblr").checked;
            var YouTube = document.getElementById("YouTube").checked;
            if (isGoing == false){
                alert("Not start yet！");
            }else{
                clearInterval(timer);
                $.ajax({
                    url:"/PauseCrawlerServlet",//要请求的服务器url
                    data:{Twitter:Twitter,Flickr:Flickr,Tumblr:Tumblr,YouTube:YouTube},
                    async:true,   //是否为异步请求
                    cache:false,  //是否缓存结果
                    type:"POST", //请求方式为POST
                    dataType:"json",   //服务器返回的数据是什么类型
                    success:function(result){  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                        if(result){
                            isGoing = false;
                            isPause = true;
                        }
                    }
                });
            }
        }

        function stop (){
            var Twitter = document.getElementById("Twitter").checked;
            var Flickr = document.getElementById("Flickr").checked;
            var Tumblr = document.getElementById("Tumblr").checked;
            var YouTube = document.getElementById("YouTube").checked;
            if (isGoing == false && isPause == false){
                alert("Not start yet！");
            }else{
                clearInterval(timer);
                $.ajax({
                    url:"/StopCrawlerServlet",//要请求的服务器url
                    data:{Twitter:Twitter,Flickr:Flickr,Tumblr:Tumblr,YouTube:YouTube},
                    async:true,   //是否为异步请求
                    cache:false,  //是否缓存结果
                    type:"POST", //请求方式为POST
                    dataType:"json",   //服务器返回的数据是什么类型
                    success:function(result){  //这个方法会在服务器执行成功是被调用 ，参数result就是服务器返回的值(现在是json类型)
                        if(result){
                            isGoing = false;
                            isPause = false;
                        }
                    }
                });
            }
        }

        window.onbeforeunload = function() {
            if(isStop() == false) {

                return "Please stop the crawler before leave.";
            }
        }
    </script>
  </head>
  <body>
  <header>
    <span>Multi-platform Data Crawler</span>
    <%--<div class="green_button" style="position:fixed;top:40px;left:0px;">--%>
      <%--<a href="#" onclick="window.location.href='http://localhost:5000'">网页爬取</a>--%>
      <%--<a href="#" onclick="window.location.href='http://localhost:8080'">Api爬取</a>--%>
    <%--</div>--%>
  </header>
  <aside >
    <ul class="menu">
      <li class="active" ><img class="icon" src="../image/left/dataGet.png"><a href="bytime.jsp" onclick="return isStop()">Crawl By Time</a></li>
      <li><img class="icon" src="../image/left/dataPreProcess.png"><a href="bykeyword.jsp" onclick="return isStop()">Crawl By Keyword</a></li>
      <li><img class="icon" src="../image/left/dataIndex.png"><a href="byregion.jsp" onclick="return isStop()">Crawl By Region</a></li>
      <li><img class="icon" src="../image/left/dataQuery.png"><a href="dataMng.jsp" onclick="return isStop()">Data Manage</a></li>
      <li></li>
      <li><img class="icon" src="../image/left/home.png"><a href="index.jsp" onclick="return isStop()">Back Home</a></li>
    </ul>
  </aside>
  <section id="main">
    <section id="con">
        <div class="content">
          <div id="params"  style="display: block">
            <label>Time:&nbsp;&nbsp;<input id="time" type="text" name="time" >&nbsp;&nbsp;min&nbsp;&nbsp;</label>
          </div>
          <label>
            Platform：&nbsp;&nbsp;
            <input id="Twitter"  type="checkbox" value="Twitter" />&nbsp;&nbsp;Twitter&nbsp;&nbsp;
            <input id="Flickr"  type="checkbox" value="Flickr" />&nbsp;&nbsp;Flickr&nbsp;&nbsp;
            <input id="Tumblr"  type="checkbox" value="Tumblr" disabled="disabled" style="TEXT-DECORATION: line-through" title="Does not support real-time acquisition
"/><s>&nbsp;&nbsp;Tumblr&nbsp;&nbsp;</s>
            <input id="YouTube"  type="checkbox" value="YouTube" />&nbsp;&nbsp;YouTube&nbsp;&nbsp;
          </label>
        </div>
        <div class="content">
          <label><input id="start" class="green_button" type="button"  value="Start" style="height:40px;width:80px;" onclick="start();"/> </label>
          <label><input id="pause" class="green_button" type="button" value="Pause" style="height:40px;width:80px;" onclick="pause();"/> </label>
          <label><input id="stop" class="green_button" type="button"  value="Stop" style="height:40px;width:80px;" onclick="stop();"/> </label>
        </div>
      <div  class="content">
        <textarea id="data" readonly></textarea>
      </div>
      <div class="content">
        <label id="timeCost">Time Cost：&nbsp;&nbsp;0:0</label>
        <progress max="100" value="0" id="pg"></progress>
      </div>
    </section>
  </section>
  </body>
</html>
