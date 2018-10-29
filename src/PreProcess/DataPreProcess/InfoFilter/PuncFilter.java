package PreProcess.DataPreProcess.InfoFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤掉任何含标点符号的单词（适用于英文）
 */
public class PuncFilter implements InfoFilter {
    @Override
    public List<String> filterInfo(List<String> text) {
        int i = text.size()-1;
        while(i>=0){
            if(containPunc(text.get(i))){
                text.remove(i);
            }
            i--;
        }
        return text;
    }
    private boolean containPunc(String str) {
        for(char c :str.toCharArray()){
            if((c>='!'&&c<='/')||(c>=':'&& c<='@')||(c>='[' && c<= '`')||(c>='{' && c<='~')) return true;
            //含有数字的
            if(c>='0' && c<='9') return true;
        }
        return false;
    }

    public static void main(String [] agrs){
        List<String> list = new ArrayList<>();
        list.add("haha!haha");
        list.add("nihao");
        list.add("lue!lue");
        list.add("k.k//");
        System.out.println(new PuncFilter().filterInfo(list));
    }
}
