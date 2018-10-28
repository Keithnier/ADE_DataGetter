package IRTree.test;

import com.mongodb.util.JSONParseException;
import org.bson.Document;
import util.FileSystem;

import java.io.*;
import java.text.ParseException;

/**
 * 根据发布内容长度分析
 */
public class DataAnalysis3 {

    public static void main(String[] args) throws IOException, ParseException {
        File dir = new File("H:\\Twitter数据\\outputfile1000\\outputfile");
        File[] fileList = dir.listFiles();

        BufferedWriter bufferedWriter = FileSystem.getBufferedWriterByPath("H:\\Twitter数据\\outputfile1000\\result3.txt");;

        for (int i = 0; i < fileList.length; i++) {
            FileReader fileReader = new FileReader(fileList[i]);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String jsonStr = null;
            int count = 0;

            while ((jsonStr = bufferedReader.readLine()) != null){
                Document document = null;
                try{
                    document = Document.parse(jsonStr);
                }catch (JSONParseException e){
                    break;
                }

                String text = (String)document.get("text");
                if (text == null){
                    continue;
                }

                bufferedWriter.write(text);
                bufferedWriter.newLine();
                System.out.println("i = " + i +", " + count++);
                //统计
            }
        }
        bufferedWriter.close();

    }
}
