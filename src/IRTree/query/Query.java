package IRTree.query;

import util.Constants;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Vector;

/**
 * @description 查询类，查询的辅助方法
 * @author Pulin Xie
 * @date 2018.5.3
 */
public class Query {
    /**
     * @param keys 查询关键字的字符形式
     * @param dictionaryFilePath 关键字与id映射表
     * @return
     * @description 通过查询关键字，找到关键字对应id值
     */
    public static Vector<Integer> findKeyId(String[] keys, String dictionaryFilePath) {
        Vector<Integer> queryWords = new Vector<>();
        HashMap<String, Integer> dictionary;
        // 只用创建一次字典表
        if (Constants.dictionary == null) {
            Constants.dictionary = new HashMap<>();
            dictionary = Constants.dictionary;
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(new FileInputStream(dictionaryFilePath)));
                String line;
                String[] temp;
                int cnt = 0; //字典从0开始
                while ((line = in.readLine()) != null) {
                    dictionary.put(line, cnt);
                    cnt += 1;
                }
                in.close();
            } catch (IOException e) {
                System.err.println("Can't open dictionary");
                System.exit(-1);
            }
        } else {
            dictionary = Constants.dictionary;
        }
        for (String key : keys) {
            Integer id = dictionary.get(key);
            if (id != null) queryWords.add(id);
        }
        return queryWords;
    }
}
