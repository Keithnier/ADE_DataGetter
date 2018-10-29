package IRTree.test;

import org.bson.Document;
import org.bson.json.JsonParseException;
import DataCrawler.util.FileSystem;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Twitter数据分析
 */
public class TwitterDataAnalysis {

    private static String testDir = "H:\\Twitter数据\\DataMaker";
    private static String dataDir = "H:\\Twitter数据\\sevendays\\outputfile";

    public static void main(String[] args) throws IOException,ParseException{
        File dir = new File(dataDir);
        File[] fileList = dir.listFiles();

        Map<Long,Integer> timeMap = new TreeMap<>(new MapKeyComparator());//1
        Map<String, Integer> locationMapByCountry_Code = new HashMap<>();//2
        Map<String, Integer> locationMapByPlace_type = new HashMap<>();//2
        Map<String, Integer> locationMapByFull_Name = new HashMap<>();//2
        Map<String, Integer> sourceMap = new HashMap<>();//4

        BufferedWriter bufferedWriter = null;
        String fileName = "H:\\Twitter数据\\sevendays\\text.txt";
        bufferedWriter = FileSystem.getBufferedWriterByPath(fileName);

        try {
            for (int i = 0; i < fileList.length; i++) {
                FileReader fileReader = new FileReader(fileList[i]);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String jsonStr = null;
                int count = 0;

                while ((jsonStr = bufferedReader.readLine()) != null) {
                    Document document = null;
                    try {
                        document = Document.parse(jsonStr);
                    } catch (JsonParseException e) {
                        break;
                    }

                    count++;
                    //1\by location
                    Document document1 = (Document) document.get("place");
                    if (document1 == null) {
                        continue;
                    }
                    String countryCode = (String) document1.get("country_code");
                    String placeType = (String) document1.get("place_type");
                    if (countryCode.equals("US")) {
                        String fullName = (String) document1.get("full_name");
                        String[] fullNameStr = fullName.split(",");
                        if (fullNameStr.length == 2) {
                            fullName = fullNameStr[1];
                        }
                        //统计
                        //location by US full name
                        storeToMap(locationMapByFull_Name, fullName);

                    }
                    //统计
                    //location by countrycode
                    storeToMap(locationMapByCountry_Code, countryCode);
                    //location by place type
                    storeToMap(locationMapByPlace_type, placeType);

                    //2\by time
                    String dateStr = (String) document.get("created_at");
                    SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmm");
                    df2.setTimeZone(TimeZone.getTimeZone("GMT"));
                    Long newDate = null;
                    int addNum = 0;

                    if (dateStr == null) {
                        Document document2 = (Document) document.get("limit");
                        if (document2 == null) {
                            System.out.println(jsonStr);
                            return;
                        }
                        String timeStamp = (String) document2.get("timestamp_ms");
                        newDate = Long.parseLong(df2.format(new Date(Long.parseLong(String.valueOf(timeStamp)))));
                        addNum = (int) document1.get("track");
                        System.out.println();
                    } else {
                        //格式转换，精确到分钟
                        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
                        Date date = df1.parse(dateStr);
                        newDate = Long.parseLong(df2.format(date));
                        addNum = 1;
                    }
                    //统计
                    if (timeMap.containsKey(newDate)) {
                        int oldValue = timeMap.get(newDate);
                        timeMap.put(newDate, oldValue + addNum);
                    } else {
                        timeMap.put(newDate, addNum);
                    }

                    //3\by source
                    String source = (String) document.get("source");
                    //统计
                    storeToMap(sourceMap, filterHtml(source));

                    //4\by text
                    String text = (String) document.get("text");
                    bufferedWriter.write(text);
                    bufferedWriter.newLine();

                }
                System.out.println("i = " + i + ", Total = " + fileList.length);
            }
        }finally {
            bufferedWriter.close();
        }
        storeToFile(timeMap,"time",1);
        storeToFile(locationMapByCountry_Code,"countrycode",2);
        storeToFile(locationMapByPlace_type,"placetype",2);
        storeToFile(locationMapByFull_Name,"fullname",2);
        storeToFile(sourceMap,"source",2);
//        storeToFile(textList,"text");
    }

    //type = 1 by time,type = 2 by location
    public static void storeToFile(Map map,String name,int type) throws IOException{
        //输出
        BufferedWriter bufferedWriter = null;
        String fileName = "H:\\Twitter数据\\sevendays\\" + name + ".txt";
        if (type == 1){
            Long flag = 0L;
            int number = 0;
            try{
                bufferedWriter = FileSystem.getBufferedWriterByPath(fileName);
                Iterator<Map.Entry<Long, Integer>> entries = map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<Long, Integer> entry = entries.next();
                    if (flag == 0L){
                        flag = entry.getKey();
                        number = entry.getValue();
                        continue;
                    }
                    if (entry.getKey() - flag <= 5){
                        number += entry.getValue();
                    }else if (entry.getKey() - flag > 5) {
                        String result = flag + " " + number;
                        bufferedWriter.write(result);
                        bufferedWriter.newLine();
                        System.out.println("Time = " + flag + ", Number = " + number);
                        flag = entry.getKey();
                        number = entry.getValue();
                    }
//                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
            }finally {
                String result = flag + " " + number;
                bufferedWriter.write(result);
                bufferedWriter.close();
            }
        }else{
            try{
                bufferedWriter = FileSystem.getBufferedWriterByPath(fileName);
                Iterator<Map.Entry<String, Integer>> entries = map.entrySet().iterator();
                while (entries.hasNext()) {
                    Map.Entry<String, Integer> entry = entries.next();
                    bufferedWriter.write(entry.getKey() + "," + entry.getValue());
                    bufferedWriter.newLine();
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                }
            }finally {
                bufferedWriter.close();
            }
        }

    }

    public static void storeToFile(ArrayList<String> list,String name) throws IOException{
        //输出
        BufferedWriter bufferedWriter = null;
        String fileName = "H:\\Twitter数据\\sevendays\\" + name + ".txt";

        try{
            bufferedWriter = FileSystem.getBufferedWriterByPath(fileName);
            for (String str : list){
                bufferedWriter.write(str);
                bufferedWriter.newLine();
            }
        }finally {
            bufferedWriter.close();
        }
    }



    public static void storeToMap(Map<String,Integer> map,String key){
        if (map.containsKey(key)){
            int oldValue = map.get(key);
            map.put(key,oldValue+1);
        }else {
            map.put(key,1);
        }
    }

    /**
     *
     * 基本功能：过滤所有以"<"开头以">"结尾的标签
     * <p>
     *
     * @param str
     * @return String
     */
    public static String filterHtml(String str) {
        String regxpForHtml = "<([^>]*)>"; // 过滤所有以<开头以>结尾的标签
        Pattern pattern = Pattern.compile(regxpForHtml);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        boolean result1 = matcher.find();
        while (result1) {
            matcher.appendReplacement(sb, "");
            result1 = matcher.find();
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}

class MapKeyComparator implements Comparator<Long>{

    @Override
    public int compare(Long num1, Long num2) {

        return (int)(num1 - num2);
    }
}
