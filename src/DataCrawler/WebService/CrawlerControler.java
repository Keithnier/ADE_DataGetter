package DataCrawler.WebService;

import DataCrawler.model.Crawler;
import twitter4j.JSONObject;
import DataCrawler.util.FileSystem;
import static CommonUtil.MongoDB.Write.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * 爬虫控制器。每个爬虫对应一个控制器。
 * 记录爬虫的状态，控制爬虫的开始、暂停和结束。
 */
public class CrawlerControler implements Runnable {
    private Crawler crawler;
    private String crawlerType;
    private volatile int dataCount = 0;
    private volatile String dataSize = "0MB";
    private volatile double duration = 0;
    private String startTime;
    private String endTime;
    private String colname;
    private String fileName;
    private boolean isRunnig = false;
    private boolean isPause = false;
    private ExecutorService executorService;

    //这种重复的东西 应该提取出来
    //TODO
    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    private SimpleDateFormat df2show = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    private Map<String, Object> params;

    public CrawlerControler(ExecutorService executorService, Crawler crawler, String crawlerType, Map<String, Object> params){
        this.executorService = executorService;
        this.crawler = crawler;
        this.crawlerType = crawlerType;
        this.params = params;
    }

    private void clearStatus(){
        dataCount = 0;
        dataSize = "0MB";
        duration = 0;
    }

    public void setParams(Map<String, Object> params){
        this.params = params;
        params.put("path", CrawlerControler.class.getClassLoader().getResource("/").getPath());
    }

    public void setCrawlerType(String crawlerType) {
        this.crawlerType = crawlerType;
    }

    public void startCrawler(){
        isRunnig = true;
        isPause = false;
        if(isPause == false) {
            clearStatus();
            startTime = df.format(new Date());
            endTime = startTime;
            params.put("startTime", startTime);

        }
        colname = crawler.getCrawlerName() + "_manage";
        executorService.execute(this);
    }

    public Map<String, String> getStatus(){
        Map<String, String> status = new HashMap<>();
        status.put("crawlerType", crawlerType);
        status.put("dataCount", String.valueOf(dataCount));
        status.put("duration", String.valueOf(duration));
        status.put("dataSize", dataSize);
        try {
            status.put("startTime", df2show.format(df.parse(startTime)));
            status.put("endTime", df2show.format(df.parse(endTime)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        status.put("crawler", crawler.getCrawlerName());
        status.put("fileName", fileName);

        return status;
    }

    public void pauseCrawler(){
        isRunnig = false;
        isPause = true;
    }

    public void stopCrawler(){
        isRunnig = false;
        isPause = false;
    }

    public void saveStatusToDB(){
        JSONObject jsonObject = new JSONObject(getStatus());
        write2DataManage(jsonObject.toString(), colname);

    }

    public void saveStatusToFile(){
        Map<String, String> status = getStatus();
        String filePath_test = CrawlerControler.class.getClassLoader().getResource("/").getPath() + "DataCrawler/outputfile/" +  crawler.getCrawlerName() + "test_" + startTime + ".txt";
        try {
            FileSystem.writeParamsToFile(filePath_test, status);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("save status fail");
        }
    }

    @Override
    public void run() {

        Map<String, Object> result = null;

        while(isRunnig && !Thread.currentThread().isInterrupted()) {

            try {
                //Location对应bytime.jsp
                if (crawlerType.equals("Location")) {
                    result = crawler.getDataByLocation(Integer.parseInt((String)params.get("west_long")),
                            Integer.parseInt((String)params.get("south_lat")), Integer.parseInt((String)params.get("east_long")),
                            Integer.parseInt((String)params.get("north_lat")), params);
                } else if (crawlerType.equals("Keyword")) {
                    result = crawler.getDataByKeyword((String) params.get("keyword"), params);
                }


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(result == null || result.size() == 0) continue;
            dataCount += (int) result.get("dataCount");
            dataSize = (String) result.get("dataSize");
            duration = (double) result.get("duration");
            fileName = (String) result.get("fileName");
        }

        if(isPause == false) {
            endTime = df.format(new Date());
            saveStatusToDB();
            saveStatusToFile();
        }

    }
}
