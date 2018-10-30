package DataCrawler.Twitter;

import com.mongodb.client.MongoCollection;
import DataCrawler.model.TwitterParam;
import org.bson.Document;
import twitter4j.*;
import DataCrawler.util.ConfigurationFactory;
import static CommonUtil.MongoDB.DataBaseUtil.getMongoCollection;

import static CommonUtil.MongoDB.Write.writeJson2Collection;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TwitterStream {

    //twitter账号信息列表
    private static List<TwitterParam> twitterParamList = new ArrayList<>();
    //private static StatusListener listener;
//    public static void stop () {
//        try {
//            listener.stop();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//    }

    /**
     * 获取Twitter
     * @param west_long 西经
     * @param south_lat 南纬
     * @param east_long 东经
     * @param north_lat 北纬
     * @param filePath  用户信息保存的文件路径
     * @param twitterCount  一次调用Api 爬取的Twitter数量
     * @throws IOException
     */
    public static void getTwitter(int west_long,int south_lat, int east_long, int north_lat, String filePath, int twitterCount) throws IOException {

        //解析twitter用户信息
        readTwitterParam(filePath);

        //去除第一个用户信息，并获取相应的流
        TwitterParam twitterParam = twitterParamList.get(0);
        twitter4j.TwitterStream twitterStream = ConfigurationFactory.getTwitterInstance(twitterParam);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH：mm：ss");
        SimpleDateFormat df4db = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = df.format(new Date());
        String colname = df4db.format(new Date());
        String filePath_data = "DataCrawler/outputfile/" + startTime + ".txt";
        String filePath_test = "DataCrawler/outputfile/" + "test_" + startTime + ".txt";
        //准备文件1，用来保存内容
        File file = new File(filePath_data);
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        //准备文件2， 用来保存其他信息
        File file2 = new File( filePath_test);
        if (!file2.exists()) {
            file2.createNewFile();
        }
        FileWriter fileWriter2 = new FileWriter(file2);
        BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
        MongoCollection<Document> collection = getMongoCollection("TwitterData", colname);

        //twitter4j用法， 为流添加监听事件
        StatusListener listener = new StatusListener() {
            private int count = 0;
            @Override
            public void onStatus(Status status) {
                try {
                    String str = TwitterObjectFactory.getRawJSON(status);
                    count++;
                    if (count > twitterCount) {//< 可以通过修改这里的常数来调整所获得的twitter的数目。
                        bufferedWriter.close();

                        // 源数据导入数据库
//                        MongoDB.Mongodb(fileName, "TwitterData",startTime.toString());

//                        System.exit(0);
                        //结束抓取数据
                        twitterStream.clearListeners();
                        twitterStream.shutdown();
                        //写入统计数据
                        bufferedWriter2.write("start time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(startTime));
                        bufferedWriter2.newLine();
                        bufferedWriter2.write("end time:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        bufferedWriter2.newLine();
                        bufferedWriter2.write("get twitter number:" + (count-1));
                        bufferedWriter2.newLine();
                        bufferedWriter2.write("get data size:" + 1.0*file.length()/(1024*1024)+"MB");
                        bufferedWriter2.close();

//                       Thread th=Thread.currentThread();
//                       System.out.println("GetData当前线程"+ count1+":"+th);
//                        th.interrupt();
//                        try {
//                            finalize();
//                        } catch (Throwable throwable) {
//                            throwable.printStackTrace();
//                        }
                        return;
                    }
                   writeJson2Collection(collection, str);
                    bufferedWriter.write(str);
                    //System.out.println("Write to outputFile/getTweets.txt " + count + " lines.");
                    System.out.println("Write to " + filePath_data +" "+ count + " lines.");
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            @Override
            public void onTrackLimitationNotice(int i) {
            }

            @Override
            public void onScrubGeo(long l, long l1) {
            }

            @Override
            public void onStallWarning(StallWarning stallWarning) {
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        };
        FilterQuery filtro = new FilterQuery();//< 设定过滤器。
        double[][] bb = {{west_long, south_lat}, {east_long, north_lat}};//< {{经度, 纬度},{经度, 纬度}}
        filtro.locations(bb);//< 设定过滤的范围，以经纬度表示。
        filtro.language("en");//< 设定语言，此处表示为英语。
        twitterStream.addListener(listener);
        twitterStream.filter(filtro);//< 将过滤器加入。

    }

    /**
     * description:读取配置文件中的账号信息，解析至twitterParamList
     */
    private static void readTwitterParam(String filePath) {
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String str = null;
        try {
            while ((str = bufferedReader.readLine()) != null) {
                String[] paramStr = str.split(",");
                if (paramStr.length != 5){
                    System.out.println("Twitter param format error!");
                    return;
                }
                twitterParamList.add(new TwitterParam(paramStr[1],paramStr[2],paramStr[3],paramStr[4]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}