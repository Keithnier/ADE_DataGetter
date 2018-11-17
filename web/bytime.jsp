
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html>
  <head>
    <title>Multi-platform Data Crawler</title>
    <link href="css/dataCrawl.css" rel="stylesheet" type="text/css"/>
    <script src="js/jquery.min.js"></script>
    <script src="js/jquery.form.js"></script>
    <script src="js/echarts.min.js"></script>
    <script src="js/echarts-wordcloud.js"></script>
    <script type="text/javascript">

        var timer;
        var isGoing = false;
        var isPause = false;
        var minute = 0;
        var second = 0;
        var finalResult;
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
                            /*var obj = document.getElementById("data");
                            obj.scrollTop = obj.scrollHeight; // good
                            obj.innerHTML = result;*/
                           finalResult=result;//得到结果JSON数组赋值给全局变量

                        }
                    }
                });

                 second++;
                 if ((minute * 60) + second >= time){
                     alert("Over！");
                     clearInterval(timer);
                     stop();
                     getCloud();//调用函数生成词云
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
        function getCloud(){
            var ResultJson = eval('(' + finalResult + ')');
            var dataArr=[];

            for(var i in ResultJson){
                var textArr=ResultJson[i].text.split(" ");
                for(var j = 0;j<textArr.length;j++){
                    var data = {};
                    data["name"]=textArr[j];
                    data["value"]=4000;
                    dataArr.push(data);
                }
            }
            var chart = echarts.init(document.getElementById('wordcloud'));
            var option = {
                tooltip: {},
                series: [ {
                    type: 'wordCloud',
                    gridSize: 2,
                    sizeRange: [12, 50],
                    rotationRange: [-90, 90],
                    shape: 'pentagon',
                    width: 600,
                    height: 400,
                    drawOutOfBound: true,
                    textStyle: {
                        normal: {
                            color: function () {
                                return 'rgb(' + [
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160),
                                    Math.round(Math.random() * 160)
                                ].join(',') + ')';
                            }
                        },
                        emphasis: {
                            shadowBlur: 10,
                            shadowColor: '#333'
                        }
                    },
                    data:dataArr
                } ]
            };
            chart.setOption(option);
            window.onresize = chart.resize;

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
      <div  class="wordcloud" id="wordcloud" style="width:560px;height:370px;margin:0 auto;border:solid white">
         <p style="font-size:55px;color: #9c9c9c;margin-left:25%;margin-top:25%">WordCloud</p>
      </div>
      <div class="content">
        <label id="timeCost">Time Cost：&nbsp;&nbsp;0:0</label>
        <progress max="100" value="0" id="pg"></progress>
      </div>
    </section>
  </section>
  </body>
</html>
