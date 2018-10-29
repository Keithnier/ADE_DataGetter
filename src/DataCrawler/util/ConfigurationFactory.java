package DataCrawler.util;

import DataCrawler.model.RestParam;
import DataCrawler.model.TwitterParam;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 获取配置信息
 */
public class ConfigurationFactory {

    /**
     * get TwitterStream
     */
    public static TwitterStream getTwitterInstance(TwitterParam twitterParam){
        ConfigurationBuilder cb = new ConfigurationBuilder();
        /*
         *设定ConfigurationBuilder的参数，各个秘钥，以及代理。
         */
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(twitterParam.getConsumerKey())
                .setOAuthConsumerSecret(twitterParam.getConsumerSecret())
                .setOAuthAccessToken(twitterParam.getAccessToken())
                .setOAuthAccessTokenSecret(twitterParam.getAccessTokenSecret())
                 .setHttpProxyHost("127.0.0.1")
                 .setHttpProxyPort(3676);

        cb.setJSONStoreEnabled(true);//< 设定获得的数据的形式，此处令其导出为json的格式。
        TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
        TwitterStream twitterStream = tf.getInstance();
        return twitterStream;
    }

    /**
     * 解析用户信息至用户信息模版
     * @param filePath Rest Api 用户信息保存的文件路径
     * @return 一个存有用户信息的类的链表
     */
    public static List<RestParam> getRestParam(String filePath){
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        List<RestParam> restParams = new ArrayList<>();
        try {
            fileReader = new FileReader(filePath);
            bufferedReader = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String str = null;
        try {
            while ((str = bufferedReader.readLine()) != null) {
                String[] paramStr = str.split(" ");
                if (paramStr.length != 6){
                    System.out.println("param format error!");
                    return restParams;
                }
                restParams.add(new RestParam(paramStr[1],paramStr[2],paramStr[3],paramStr[4], paramStr[5]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return restParams;
    }

    /**
     * 解析用户信息至用户信息模版//Youtube
     * @param filePath Rest Api 用户信息保存的文件路径
     * @return 一个存有用户信息的类的链表
     */
    public static String getYouTubeRestParam(String filePath){
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
            str = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;
    }

}
