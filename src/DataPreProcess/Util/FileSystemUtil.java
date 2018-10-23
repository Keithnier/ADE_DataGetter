package DataPreProcess.Util;

public class FileSystemUtil {

    public static String getFullDataPath(String fileName){
        String path = FileSystemUtil.class.getClassLoader().
                getResource("DataPreProcess/DataRes/").getPath();

        return path + fileName;
    }

    public static void main(String[] str) {
        System.out.println(getFullDataPath("abc.txt"));
    }
}
