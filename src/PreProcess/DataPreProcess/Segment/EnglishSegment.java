package PreProcess.DataPreProcess.Segment;

import PreProcess.DataPreProcess.InfoFilter.*;
import PreProcess.DataPreProcess.Util.FileSystemUtil;
import PreProcess.DataPreProcess.Util.PropertyUtil;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class EnglishSegment implements Segment {

    private Properties props;//声明使用的属性
    private  StanfordCoreNLP pipeline;//斯坦福的分词器包

    //构造函数
    public EnglishSegment(){
        super();
        props = new Properties();  // set up pipeline properties
        props.put("annotators", "tokenize, ssplit, pos, lemma");   //分词、分句、词性标注和次元信息。
        pipeline = new StanfordCoreNLP(props);
    }

    @Override
    public List<String> segment(String content) {
        //进行第一遍原始分词
        List<String> segResult = new ArrayList<>();
        Annotation document = new Annotation(content);
        this.pipeline.annotate(document);
        List<CoreMap> words = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap word_temp: words) {
            for (CoreLabel token:word_temp.get(CoreAnnotations.TokensAnnotation.class)) {
                String lema = token.get(CoreAnnotations.LemmaAnnotation.class);  // 获取对应上面word的词元信息，即我所需要的词形还原后的单词
                segResult.add(lema.toLowerCase());
            }
        }

        //第三遍整合数据，将所有词汇设置成为小写
        //第四遍去除停用词
        return segResult;
    }

    /**
     * 去除停用词
     * @param StopWordPath 停用词路径
     * @param wordlist 单词列表
     * @return 去除停用词后的单词列表
     */
    @Override
    public List<String> removeStopword(String StopWordPath, List<String> wordlist) {
        return StopWords.__removeStopwords__(StopWordPath,wordlist);
    }
    public List<String> removeStopword( List<String> wordlist) {
        String StopWordPath = FileSystemUtil.getFullDataPath(PropertyUtil.getValueByKey("stopwordpath"));
        return StopWords.__removeStopwords__(StopWordPath,wordlist);
    }

    @Override
    public List<String> doAll(String content, String StopWordPath) {
        List<String> segResult = segment(content);
        //第二遍过滤掉所有中文词汇，网址，表情，用户，主题，含标点符号的词
        segResult = new LanguageFilter(LanguageFilter.LANG_CHINESE).filterInfo(segResult);
        segResult = new HttpFilter().filterInfo(segResult);
        segResult = new EmojiFilter().filterInfo(segResult);
        segResult = new UserFilter().filterInfo(segResult);
        segResult = new ThemeFilter().filterInfo(segResult);
        segResult = new PuncFilter().filterInfo(segResult);
        return removeStopword(StopWordPath,segResult);
    }
    @Override
    public List<String> doAll(String content) {
        List<String> segResult = segment(content);
        //第二遍过滤掉所有中文词汇，网址，表情，用户，主题，含标点符号的词
        segResult = new LanguageFilter(LanguageFilter.LANG_CHINESE).filterInfo(segResult);
        segResult = new HttpFilter().filterInfo(segResult);
        segResult = new EmojiFilter().filterInfo(segResult);
        segResult = new UserFilter().filterInfo(segResult);
        segResult = new ThemeFilter().filterInfo(segResult);
        segResult = new PuncFilter().filterInfo(segResult);
        return removeStopword(segResult);
    }

    public static void main(String[] args){
        String str = "abc def ghi";
        EnglishSegment eg = new EnglishSegment();
        List<String> list = eg.doAll(str);
        for(String ss:list){
            System.out.println(ss);
        }
    }
}
