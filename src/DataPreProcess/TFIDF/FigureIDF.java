package DataPreProcess.TFIDF;


import DataPreProcess.Util.FileSystemUtil;
import DataPreProcess.Util.PropertyUtil;

import java.io.*;
import java.util.*;

import static DataPreProcess.Dictionary.LoadDictionary.loadDictionary;

public class FigureIDF {

    public static void figureIDF(int TypeCode){
        switch (TypeCode){
            case PreCut.TWITTER:
                __twitterFigureIDF__();
                break;
            case PreCut.YOUTUBE:
                break;
            case PreCut.FLICKR:
                break;
            case PreCut.TUMBLR:
                break;
            default:
                break;
        }
    }

    public static void __twitterFigureIDF__(){
        //加载词典
        List<String> dictionary = loadDictionary(FileSystemUtil.getFullDataPath(
                PropertyUtil.getValueByKey("dictionarypath")));
        //打开已经切分好的文件
        String cachePath = FileSystemUtil.class.getClassLoader().
                getResource("Cache/").getPath();
        BufferedReader br;
        PrintStream ps;
        try{
            ps = new PrintStream(new FileOutputStream(cachePath+"__IDF__"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            System.out.println("缓存文件损坏！");
            return;
        }
        //计算每一个词的IDF值
        for(String word:dictionary){
            try{
                br = new BufferedReader(new FileReader(cachePath+"__preCutTwitterSeg__"));
                int docCount = 0; //记录文档的总数
                int haswordCount = 0; //记录含有某个word的文档数
                while(true){
                    String words;
                    words = br.readLine();
                    //读取完毕后进行计算IDF值
                    if(words==null) break;
                    docCount++;
                    words = words.replace("\n","");
                    words = words.replace("\r","");
                    List<String> wordlist = new ArrayList<String>(Arrays.asList(words.split(":")));
                    if(wordlist.contains(word)) haswordCount++;
                }
                //计算idf
                ps.println(word+":"+String.valueOf(Math.log(1.0d*docCount/(haswordCount+1.0))));
                br.close();
            }catch (IOException e){
                e.printStackTrace();
                ps.close();
                return;
            }
        }
        ps.close();
    }

    public static Map<String,Double> makeIDFDic(){
        //缓冲路径
        String cachePath = FileSystemUtil.class.getClassLoader().
                getResource("Cache/").getPath();
        BufferedReader br ;
        //字典-IDF值得MAP
        Map<String,Double> map = new HashMap<>();
        try{

            br = new BufferedReader(new FileReader(cachePath+"__IDF__"));
            String elem;
            while((elem = br.readLine())!=null){
                elem = elem.replace("\r","").replace("\n","");
                String[] strlist = elem.split(":");
                map.put(strlist[0],Double.parseDouble(strlist[1]));

            }
            br.close();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return map;
    }
}
