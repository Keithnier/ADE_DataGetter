package PreProcess.DataPreProcess.Util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtil {

    /**
     * 获取属性的配置文件
     * @param filePath 文件路径
     * @param key 属性名
     * @return 属性值
     */
    public static String getValueByKey(String filePath, String key) {
        Properties pps = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            pps.load(in);
            System.out.println("正在获取属性"+key);
            String value = pps.getProperty(key);
            System.out.println("获取属性成功: "+key+"="+value);
            return value;
        }catch (IOException e) {
            System.out.println("获取属性"+key+"失败！");
            e.printStackTrace();
            return null;
        }
    }

    //使用默认的配置文件位置
    public static String getValueByKey(String key) {
        String path = FileSystemUtil.getFullDataPath("config.properties");
        return getValueByKey(path,key);
    }

}
