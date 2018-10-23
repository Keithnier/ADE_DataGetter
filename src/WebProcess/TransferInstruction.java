package WebProcess;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 将前端指令转化为map
 */
public class TransferInstruction {
    public static Map<String, BySentence.FILTER> Check2Map(HttpServletRequest req){
        Map<String, BySentence.FILTER> map = new HashMap<>();
        if(req.getParameter("rs").toLowerCase().equals("true"))
            map.put("rs", BySentence.FILTER.STOPWORDS);
        if(req.getParameter("fe").toLowerCase().equals("true"))
            map.put("fe",BySentence.FILTER.EMOJI);
        if(req.getParameter("fh").toLowerCase().equals("true"))
            map.put("fh", BySentence.FILTER.HTTP);
        if(req.getParameter("fc").toLowerCase().equals("true"))
            map.put("fc",BySentence.FILTER.LANGUAGE);
        if(req.getParameter("ft").toLowerCase().equals("true"))
            map.put("ft", BySentence.FILTER.THEME);
        if(req.getParameter("fu").toLowerCase().equals("true"))
            map.put("fu",BySentence.FILTER.USER);
        return map;
    }
}
