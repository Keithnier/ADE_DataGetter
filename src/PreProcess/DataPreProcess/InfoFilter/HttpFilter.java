package PreProcess.DataPreProcess.InfoFilter;

import java.util.List;

public class HttpFilter implements InfoFilter {
    @Override
    public List<String> filterInfo(List<String> text) {
        int i = text.size() - 1;
        while (i >= 0) {
            if (isHttpUrl(text.get(i))) {
                String res = text.remove(i);
                //System.out.println("Remove HTTP: " + res);
            }
            i--;
        }
        return text;
    }

    private boolean isHttpUrl(String text) {
        if (text.trim().startsWith("http")) {
            return true;
        } else {
            return false;
        }
    }
}
