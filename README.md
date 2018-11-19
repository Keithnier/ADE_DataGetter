# DataGetter
该代码主要包含三部分内容

1. 数据爬取部分
2. 数据预处理部分
3. 索引建立



主要代码在**src**中

web相关文件在**web**文件夹下

相关库文件在**web/WEB-INF/lib**下

其他配置文件，如IDEA配置文件，及其他带有个人信息的配置文件未上传



主目录结构如下
``` shell
ADE_DataGetter
├── SaveData
├── src
└── web  
```


src目录结构如下

``` shell
src
├── CommonUtil
├── DataCrawler
├── IRTree
└── PreProcess
```

**DataCrawler**对应爬虫部分  
**IRTree**为索引搜索引擎部分  
**PreProcess**为预处理部分  

如若项目无法运行，请先排除**文件缺失**的问题  
src/DataCrawler/paramfile中应有Oauth验证相关信息  
另，请在war包classes文件夹下对应DataCrawler目录下添加outputfile文件夹，避免chen
