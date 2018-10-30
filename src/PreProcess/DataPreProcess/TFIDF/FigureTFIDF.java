package PreProcess.DataPreProcess.TFIDF;

import PreProcess.DataPreProcess.Util.FileSystemUtil;
import PreProcess.DataPreProcess.Util.PropertyUtil;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static PreProcess.DataPreProcess.Dictionary.LoadDictionary.loadDictionary;

public class FigureTFIDF {

    public static void figureTF(int TypeCode){
        switch (TypeCode){
            case PreCut.TWITTER:
                __twitterFigureTF__();
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

    /**
     * 计算Twitter的tf-idf
     */
    public static void __twitterFigureTF__(){
        Map<String,Double> map = FigureIDF.makeIDFDic();//单词:IDF键值对
        List<String> dictionary = loadDictionary(FileSystemUtil.getFullDataPath(
                PropertyUtil.getValueByKey("dictionarypath")));//单词模板

        String cachePath = FileSystemUtil.class.getClassLoader().
                getResource("PreProcess/Cache/").getPath();

        PrintStream ps;//生成TF-IDF并写入到文件中
        BufferedReader br;//获取切分后的词
        try{
            ps = new PrintStream(new FileOutputStream(cachePath+"__TF_IDF__"));
            br = new BufferedReader(new FileReader(cachePath+"__preCutTwitterSeg__"));
        }catch (FileNotFoundException e){
            e.printStackTrace();
            return;
        }
        try{
            String words;
            while((words = br.readLine())!=null){
                words = words.replace("\n","");
                words = words.replace("\r","");
                //处理分词后什么都没有的情况
                if(words.equals("")){
                    continue;
                }
                String[] wordlist = words.split(":");
                Map<String,Double> wordmap = figureElemTF(wordlist);
                int count = 0;
                for(Map.Entry<String, Double> entry: wordmap.entrySet()){
                    count++;//用于控制字符串格式
                    //计算tf * idf，获取序号，将计算结果存储到缓存文件中
                    String key = entry.getKey();
                    double _value = entry.getValue();
                    assert map != null;
                    double idf = 0;
                    try{
                        idf = map.get(key);
                    }catch (NullPointerException e){
                        System.out.println("key 获取失败");
                        idf = 0;
                    }
                    _value = _value*idf; //tf*idf
                    String value = String.valueOf(_value);
                    key = String.valueOf(dictionary.indexOf(key));//获取key对应的编号
                    if(count<wordmap.size())
                        ps.print(key+":"+value+",");
                    else
                        ps.println(key+":"+value);
                }
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            ps.close();
        }
    }

    //计算一个文档的词频
    private static Map<String,Double> figureElemTF(String[] wordlist){
        Map<String, Double> map = new HashMap<>();
        //第一遍计算各个词语出现的频度
        for(String word:wordlist){
            if(word.equals("")) continue;
            //包含这个词则词语所对应的double+1
            if(map.containsKey(word)){
                String key = word;
                double value = map.get(key);
                value++;
                map.remove(key);
                map.put(key,value);
            }
            else{
                map.put(word,1.0);
            }
        }

        //第二遍各个词语的频度除以length则得到tf-idf
        Set<String> keyset = map.keySet();
        for(String key:keyset){
            double value = map.get(key);
            map.put(key,value/wordlist.length);
        }
        return map;
    }

    public static void main(String[] args){
        __twitterFigureTF__();
    }
}
