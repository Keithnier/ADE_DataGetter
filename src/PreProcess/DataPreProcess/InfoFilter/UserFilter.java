package PreProcess.DataPreProcess.InfoFilter;

import java.util.List;

/**
 * 过滤@的用户
 */
public class UserFilter implements InfoFilter {
    @Override
    public List<String> filterInfo(List<String> text) {
        int i = text.size() - 1;
        while (i >= 0) {
            if (text.get(i).toCharArray()[0]=='@') {
                String res = text.remove(i);
            }
            i--;
        }
        return text;
    }
}
