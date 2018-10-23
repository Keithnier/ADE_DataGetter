
//输入一段英文句子进行处理
function button1_click() {
    var text = $("#textarea1").val();//输入数据
    var rs = document.getElementById("removeStop").checked;
    var fe = document.getElementById("filterEmoji").checked;
    var fh = document.getElementById("filterHttp").checked;
    var fc = document.getElementById("filterChinese").checked;
    var ft = document.getElementById("filterTheme").checked;
    var fu = document.getElementById("filterUser").checked;
    if(text==""){
        alert("输入为空");
        return false;
    }
    else{
        $.ajax({
            url:"/PrePorcessServletButtonI",//要请求的服务器url
            data:{text:text, rs:rs, fe:fe, fh:fh, fc:fc, ft:ft, fu:fu},
            async:true,   //是否为异步请求
            cache:false,  //是否缓存结果
            type:"POST", //请求方式为POST
            dataType:"text",   //服务器返回的数据是什么类型
            success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
                if(result){
                    var obj = document.getElementById("result");
                    obj.scrollTop = obj.scrollHeight; // good
                    obj.innerHTML = result;
                }
            }
        });
    }
}

//输入TwitterID在数据库中查找进行处理
function button2_click() {
    var e = document.getElementById("select1");
    var textType = e.options[e.selectedIndex].text;
    var text = $("#textarea2").val();//输入数据
    var rs = document.getElementById("removeStop").checked;
    var fe = document.getElementById("filterEmoji").checked;
    var fh = document.getElementById("filterHttp").checked;
    var fc = document.getElementById("filterChinese").checked;
    var ft = document.getElementById("filterTheme").checked;
    var fu = document.getElementById("filterUser").checked;
    if(text==""){
        alert("输入为空");
        return false;
    }
    else{
        $.ajax({
            url:"/PrePorcessServletButtonII",//要请求的服务器url
            data:{text:text, rs:rs, fe:fe, fh:fh, fc:fc, ft:ft, fu:fu, textType:textType},
            async:true,   //是否为异步请求
            cache:false,  //是否缓存结果
            type:"POST", //请求方式为POST
            dataType:"text",   //服务器返回的数据是什么类型
            success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
                if(result){
                    var obj = document.getElementById("result");
                    obj.scrollTop = obj.scrollHeight; // good
                    obj.innerHTML = result;
                }
            }
        });
    }
}

function button3_click() {
    var e = document.getElementById("select2");
    var textType = e.options[e.selectedIndex].text;
    var rs = document.getElementById("removeStop").checked;
    var fe = document.getElementById("filterEmoji").checked;
    var fh = document.getElementById("filterHttp").checked;
    var fc = document.getElementById("filterChinese").checked;
    var ft = document.getElementById("filterTheme").checked;
    var fu = document.getElementById("filterUser").checked;
        $.ajax({
            url:"/PrePorcessServletButtonIII",//要请求的服务器url
            data:{rs:rs, fe:fe, fh:fh, fc:fc, ft:ft, fu:fu, textType:textType},
            async:true,   //是否为异步请求
            cache:false,  //是否缓存结果
            type:"POST", //请求方式为POST
            dataType:"text",   //服务器返回的数据是什么类型
            success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
                if(result){
                    var obj = document.getElementById("result");
                    obj.scrollTop = obj.scrollHeight; // good
                    obj.innerHTML = result;
                }
            }
        });
}


//输入TwitterID在数据库中查找进行处理
function luceneSearch() {
    var e = document.getElementById("select3");
    var textType = e.options[e.selectedIndex].text;
    var text = $("#textarea4").val();//输入数据
    if(text==""){
        alert("输入为空");
        return false;
    }
    else{
        $.ajax({
            url:"/PrePorcessLuceneSearch",//要请求的服务器url
            data:{text:text, textType:textType},
            async:true,   //是否为异步请求
            cache:false,  //是否缓存结果
            type:"POST", //请求方式为POST
            dataType:"text",   //服务器返回的数据是什么类型
            success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
                if(result){
                    var obj = document.getElementById("result");
                    obj.scrollTop = obj.scrollHeight; // good
                    obj.innerHTML = result;
                }
            }
        });
    }
}











function reSetDictionary(){
    var e = document.getElementById("select4");
    var textType = e.options[e.selectedIndex].text;
    $.ajax({
        url:"/PreProcessResetDic",
        data:{textType:textType},
        async:true,   //是否为异步请求
        cache:false,  //是否缓存结果
        type:"POST", //请求方式为POST
        success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
            if(result){
                if(result==="OK")
                    alert("建立词典成功！");
                else
                    alert("建立词典失败！");
            }
        }
    });
}

function reSetTFIDF(){
    var e = document.getElementById("select4");
    var textType = e.options[e.selectedIndex].text;
    $.ajax({
        url:"/PreProcessResetTFIDF",
        data:{textType:textType},
        async:true,   //是否为异步请求
        cache:false,  //是否缓存结果
        type:"POST", //请求方式为POST
        success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
            alert("计算TFIDF成功！");
        }
    });
}

function reSetLucene(){
    var e = document.getElementById("select4");
    var textType = e.options[e.selectedIndex].text;
    $.ajax({
        url:"/PreProcessResetLucene",
        data:{textType:textType},
        async:true,   //是否为异步请求
        cache:false,  //是否缓存结果
        type:"POST", //请求方式为POST
        success:function(result){  //这个方法会在服务器执行成功时被调用 ，参数result就是服务器返回的值(现在是text类型)
            alert("索引建立成功成功！");
        }
    });
}