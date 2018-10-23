package DataPreProcess.Segment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StopWords {
    public static Set<String> stopwords;

    public static List<String> __removeStopwords__(String StopWordPath, List<String> wordlist){
        //加载用户词典
        if(stopwords==null){
            stopwords = new HashSet<>();
            try{
                FileReader fr;
                fr = new FileReader(StopWordPath);
                BufferedReader br = new BufferedReader(fr);
                do{
                    String stopword = br.readLine();
                    if(stopword==null) break;
                    stopword = stopword.replace("\n","");
                    stopwords.add(stopword);
                }while(true);
                fr.close();
            }catch (FileNotFoundException e1){
                System.out.println("文件路径错误！");
                e1.printStackTrace();
            }catch(IOException e2){
                e2.printStackTrace();
            }
        }

        int i = wordlist.size()-1;
        while(i>=0){
            if(stopwords.contains(wordlist.get(i))){
                wordlist.remove(i);
            }
            i--;
        }
        return wordlist;
    }
    public static void main(String[] args){

    }

}
