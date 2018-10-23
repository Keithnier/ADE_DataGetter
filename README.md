# DataGetter
##数据获取工具 1.5版

对应Buglist [3.16]  


#历史版本
##v1.4
###修改
1.名字修改为DataGetter

###新增功能
1.指定爬取持续时间
2.twitter rest Api 使用
3.Flickr rest Api 使用
4.MongoDB 单线程使用

###删除功能
1.定时功能删除

###2017/11/29
1.twitter OAuth流程 加入手机验证功能

###2018/01/10
####新添加类
1.Crawler 接口  
2.CrawlerControler类 控制爬虫的启动暂停和结束  
3.CrawlerService类 爬虫控制服务，持有四个爬虫的控制器  
4.initServiceServlet 程序运行时，初始化爬虫相关服务  

####新添加功能
1.启动、暂停、停止功能

####未解决问题
1.web 启动时，因网络问题，爬虫初始化可能失败，未解决  
2.Twitter 当设置每次爬取100条时，有api限制，返回码420  
3.OAuth 验证模块独立出来  
4.目前为单例模式，多人操作存在冲突问题

# 预处理显示模块
* URL： /PreProcessServlet  

* 参数： num  显示第几个处理结果  
参数范围：1 - 12983  

* 返回结果：String类型字符串  
==============line==11=============================  
words num: 14  
words: [@jnicolelangley, yeah, but, someday, he, will, be, do, a, boob, luge, with, you, #blessed]  
emoji num: 0  
emoji: []  
http num: 0  
http: []  
words num after stem: 6  
words stem: [jnicolelangley, yeah, someday, boob, luge, blessed]  