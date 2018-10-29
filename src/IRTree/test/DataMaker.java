package IRTree.test;

import org.bson.Document;
import org.bson.json.JsonParseException;
import DataCrawler.util.FileSystem;

import java.io.*;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataMaker {
    private static String testDir = "F:\\TwitterData\\test";
    private static String dataDir = "F:\\TwitterData\\outputfile";

    public static void main(String[] args) throws IOException,ParseException {
        File dir = new File(dataDir);
        File[] fileList = dir.listFiles();

        BufferedWriter bufferedWriter = null;
        String fileName = "F:\\TwitterData\\text.txt";
        bufferedWriter = FileSystem.getBufferedWriterByPath(fileName);

        int count = 0;
        int id = 0;

        try {
            for (int i = 0; i < fileList.length; i++) {
                FileReader fileReader = new FileReader(fileList[i]);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String jsonStr = null;

                while ((jsonStr = bufferedReader.readLine()) != null) {
                    count++;
                    Document document = null;
                    String locationStr = null;
                    try {
                        document = Document.parse(jsonStr);
                        Document document1 = (Document)document.get("place");
                        Document document2 = (Document)document1.get("bounding_box");
                        locationStr = StringFilter(document2.get("coordinates").toString());
                    } catch (JsonParseException | NullPointerException e) {
                        continue;
                    }

                    String dateStr = (String) document.get("timestamp_ms");
                    String textStr = StringFilter2((String) document.get("text"));

                    if (dateStr == null || locationStr==null || textStr== null || dateStr == "" || locationStr=="" || textStr== "") {
                        continue;
                    } else {
                        String result = id + " " + dateStr + " " + locationStr + " " + textStr;
//                        System.out.println(result);
                        bufferedWriter.write(result);
                        bufferedWriter.newLine();
                        id++;
                    }
                    System.out.println("id = " + id + ", count = " +count + ", i = " + i + ", Total = " + fileList.length);

                }
//                System.out.println("i = " + i + ", Total = " + fileList.length);
            }
        }finally {
            bufferedWriter.close();
        }

    }

    public static String StringFilter(String string){
        String regEx1 = "[\\[\\] ]";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(string);
        return m.replaceAll("").trim();
    }

    public static String StringFilter2(String string){
        String regEx1 = "\n";
        Pattern p = Pattern.compile(regEx1);
        Matcher m = p.matcher(string);
        return m.replaceAll("").trim();
    }


}
