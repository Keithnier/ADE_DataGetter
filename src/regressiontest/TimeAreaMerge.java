package regressiontest;

import util._readData;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class TimeAreaMerge {

    public static final String TYPE = "day";//取值有hour和day

    public static void main(String[] args) {
        try {
            Map<String, List<String>> map = timeAreaMerge1("twitter/test/1day_after2_back.txt");
            for (Map.Entry<String, List<String>> e : map.entrySet()) {
                System.out.println("key: " + e.getKey());
                for (String s : e.getValue()) {
                    System.out.print(s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, List<String>> timeAreaMerge1(String filePath) throws IOException {
        Map<String, List<String>> resultMap = new LinkedHashMap<>();
        _readData read = new _readData(filePath);
        String line ;
        while ((line = read.myreadline(filePath) )!= null) {
            if ("false format" == line) continue;
            String timestamp = timeChange(line.split(",", 3)[1], TYPE);
            if (resultMap.containsKey(timestamp)) {
                List<String> valueList = resultMap.get(timestamp);
                valueList.add(line);
                resultMap.put(timestamp, valueList);
            } else {
                List<String> valueList = new LinkedList<>();
                valueList.add(line);
                resultMap.put(timestamp, valueList);
            }
        }

        return resultMap;
    }

    public static String timeChange(String timestamp, String type) {
        SimpleDateFormat sdf = null;
        if (type.equals("hour")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd-HH");
        } else if (type.equals("day")) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        String result = sdf.format(new Date(Long.parseLong(timestamp)));
        return result;
    }

    public static BufferedReader fileReader(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        if (file.exists() == false) {
            System.err.println("Error: File not exists!");
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        return bufferedReader;
    }

    private static int countLineNumber(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        int lineNum = 0;
        while (line != null) {
            lineNum++;
            line = bufferedReader.readLine();
        }
        return lineNum;
    }

}
