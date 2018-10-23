package DataPreProcess.InfoFilter;

import java.util.List;


/**
 * 过滤表情
 */
public class EmojiFilter implements InfoFilter {

    @Override
    public List<String> filterInfo(List<String> text) {
        int i = text.size() - 1;
        while(i >= 0) {
            if(containsEmoji(text.get(i))) {
                String res = text.remove(i);
                //System.out.println("Remove Emoji: " + res);
            }
            i--;
        }
        return  text;
    }


    private boolean isEmojiCharacter(char codePoint) {
        return !((codePoint == 0x0) ||
                (codePoint == 0x9) ||
                (codePoint == 0xA) ||
                (codePoint == 0xD) ||
                ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
                ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
    }

    private boolean containsEmoji(String source) {
        if (source.trim().equals("")) {
            return false;
        }
        for (char codePoint : source.toCharArray()) {
            if (isEmojiCharacter(codePoint)) {
                //do nothing，判断到了这里表明，确认有表情字符
                return true;
            }
        }
        return false;
    }
}
