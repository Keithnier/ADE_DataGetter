package DataPreProcess.Dictionary;


import DataPreProcess.Util.FileSystemUtil;
import DataPreProcess.Util.PropertyUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class LoadDictionary {
    public static List<String> loadDictionary(String Path){
        FileReader fr;
        ArrayList<String> Dictionary = new ArrayList<>();
        try{
            fr = new FileReader(Path);
            BufferedReader br = new BufferedReader(fr);
            do{
                String word = br.readLine();
                if(word == null) break;
                word = word.replace("\n","");
                Dictionary.add(word);
            }while(true);
            fr.close();
        }catch (Exception e1){
            System.out.println("文件路径错误！");
            e1.printStackTrace();
            Dictionary = null;
        }
        return Dictionary;
    }
    public static List<String> loadDictionary(){
        String Path = FileSystemUtil.getFullDataPath(PropertyUtil.getValueByKey("dictionarypath"));
        return loadDictionary(Path);
    }

}
