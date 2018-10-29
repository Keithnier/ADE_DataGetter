package PreProcess.DataPreProcess.InfoFilter;

import java.util.ArrayList;
import java.util.List;

/**
 *过滤语言
 * 传入：List<String> text 分词结果
 */
public class LanguageFilter implements InfoFilter {
    final public static int LANG_ENGLISH = 0;
    final public static int LANG_CHINESE = 1;
    int language;//使用语言

    /**
     * 构造器
     * @param lang 待去掉的语言
     */
    public LanguageFilter(int lang) {
        this.language = lang;
    }
    @Override
    public List<String> filterInfo(List<String> text) {
        switch(this.language){
            case LANG_CHINESE:
                return parseChinses(text);
            case LANG_ENGLISH:
                return parseEnglish(text);
        }
        return null;
    }
    /**
     * 删除英文
     * @param text 字符串列表
     * @return 删除英文后的字符串列表
     */
    private List<String> parseEnglish(List<String> text){
        int i = text.size()-1;
        while(i>=0){
            if(containEnglish(text.get(i))){
                text.remove(i);
            }
            i--;
        }
        return text;
    }
    private boolean containEnglish(String str){
        char [] strARR = str.toCharArray();
        for(char c : strARR){
            if ((c >='a'&&c<='z')||(c>='A'&&c<='Z')) return true;
        }
        return false;// 根据字节码判断
    }

    /**
     * 删除中文
     * @param text 字符串列表
     * @return 删除中文后的字符串列表
     */
    private List<String> parseChinses(List<String> text){
        int i = text.size()-1;
        while(i>=0){
            if(containChinese(text.get(i))){
                text.remove(i);
            }
            i--;
        }
        return text;
    }
    private boolean containChinese(String str){
        char[] charARR = str.toCharArray();
        for(char c:charARR){
            if((c >= 0x4e00) && (c <= 0x9fbb))
                return true;
        }
        return false;
    }

    //测试
    public static void main(String[] args){
        List<String> testList= new ArrayList<String>(){{
            add("nihao");
            add("你好");
            add("ni好");
        }};
        System.out.println(new LanguageFilter(LANG_ENGLISH).filterInfo(testList));
    }
}
