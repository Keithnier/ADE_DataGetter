package PreProcess.WebProcess;

import PreProcess.DataPreProcess.InfoFilter.*;
import PreProcess.DataPreProcess.Segment.Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BySentence {

    public enum FILTER {
        EMOJI, HTTP, LANGUAGE, THEME, USER, STOPWORDS
    }
    /**
     * 处理任意一段话
     * @param text
     * @return
     */
    public static List<String> ProcessSentence(String text, Segment seg, List<String> dic, Map<String,FILTER> map) {
        List<String> result = new ArrayList<>();
        //获取原始分词
        List<String> seg_words = seg_result(text,seg);
        //result.add(List2Str(seg_words));
        for(Map.Entry<String, FILTER> entry:map.entrySet()){
            if(entry.getKey().equals("rs"))
                seg_words = stopword_result(seg_words,seg);
            else
                seg_words = filter_result(seg_words,entry.getValue());
        }
        String index_result = index_result(dic, seg_words);
        String segRes = List2Str(seg_words);
        segRes = "RESULT:&nbsp;"+segRes;
        result.add(segRes);
        result.add(index_result);
        return result;
    }

    //切分词汇
    private static List<String> seg_result(String text, Segment seg){
        List<String> list = seg.segment(text);
        return new PuncFilter().filterInfo(list);
    }

    //去除停用词
    private static List<String> stopword_result(List<String> str, Segment seg){
        return seg.removeStopword(str);
    }

    //过滤各种词汇
    public static List<String> filter_result(List<String> wordlist, FILTER Filter_Type){
        switch (Filter_Type){
            case EMOJI:
                return new EmojiFilter().filterInfo(wordlist);
            case HTTP:
                return new HttpFilter().filterInfo(wordlist);
            case LANGUAGE:
                return new LanguageFilter(LanguageFilter.LANG_CHINESE).filterInfo(wordlist);
            case THEME:
                return new ThemeFilter().filterInfo(wordlist);
            case USER:
                return new UserFilter().filterInfo(wordlist);
            default:
                return wordlist;
        }
    }

    //List to String
    public static String List2Str(List<String> wordlist){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for(String word:wordlist){
            count++;
            sb.append(word);
            if(count<wordlist.size())
                sb.append(",");
            else
                sb.append("\n");
        }
        return sb.toString();
    }
    //获取在字典中的位置
    public static String index_result(List<String> dictionary, List<String> words){
        StringBuilder sb = new StringBuilder();
        sb.append("INDEX in DICTIONARY:&nbsp;");
        for(int i = 1;i<=words.size();i++){
            int index = dictionary.indexOf(words.get(i-1));
            sb.append(words.get(i - 1));
            sb.append(':');
            sb.append(index);
            if(i<words.size())
                sb.append(',');
            else
                sb.append('\n');

        }
        return sb.toString();
    }
}
