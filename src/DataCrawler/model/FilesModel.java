package DataCrawler.model;

/**
 * 文件Model，爬取的文本数据存为txt文件，相关信息由本模型提供
 */
public class FilesModel {
    private String fileName;
    private String startTime;
    private String dataNum;
    private String dataSize;
    private String crawlerType;
    private String crawler;

    public FilesModel(String fileName, String startTime, String dataNum, String dataSize, String crawler, String crawlerType) {

        this.fileName = fileName;
        this.startTime = startTime;
        this.dataNum = dataNum;
        this.dataSize = dataSize;
        this.crawlerType = crawlerType;
        this.crawler = crawler;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDataNum() {
        return dataNum;
    }

    public String getDataSize() {
        return dataSize;
    }

    public String getCrawlerType() { return crawlerType;}

    public String getCrawler() {
        return crawler;
    }

}
