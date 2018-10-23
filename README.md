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

└── DataGetter
    ├── SaveData
    ├── out
    ├──**outputfile**
    ├── **paramfile**
    ├── src
    └── web



src目录结构如下

src
├── DAO
├── DataPreProcess
│   ├── DataBase
│   ├── DataRes
│   │   └── Twitter
│   ├── Dictionary
│   ├── Index
│   ├── InfoExtra
│   ├── InfoFilter
│   ├── Model
│   ├── Segment
│   ├── TFIDF
│   └── Util
├── Flickr
├── Tumblr
├── Twitter
├── WebProcess
├── WebService
├── YouTube
├── invertedindex
├── model
├── neustore
│   ├── base
│   └── heapfile
├── **outputfile**
├── **paramfile**
├── query
├── regressiontest
├── servlet
├── spatialindex
│   ├── rtree
│   ├── spatialindex
│   └── storagemanager
├── test
└── util



如若项目无法运行，请先排除**文件缺失**的问题