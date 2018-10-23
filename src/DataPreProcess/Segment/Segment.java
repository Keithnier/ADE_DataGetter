package DataPreProcess.Segment;

import java.util.List;

public interface Segment {
    /**
     * 对字符串内容进行分词
     * @param content 内容
     * @return 由空格符作为分隔符的分词结果String
     */
    List<String> segment(String content);

    //去除停用词
    List<String> removeStopword(String StopWordPath, List<String> wordlist);
    List<String> removeStopword(List<String> wordlist);
    //分词然后去除停用词
    List<String> doAll(String content, String StopWordPath);
    List<String> doAll(String content);
}
