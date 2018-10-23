package util;

import java.io.*;
import java.util.Map;

/**
 *
 * description： 处理跟文件相关的操作
 *
 *
 * methods:  writeParamsToFile         将健值对信息存入对应的文件
 *           writeTextStreamToFile         将流中的信息指定的行数写入对应文件
 *           getFileByPath             根据文件路径获取文件
 *
 */
public class FileSystem {

    /**
     * 将健值对信息存入对应的文件
     * @param filePath    文件路径
     * @param params      需要写入文件的健值对
     * @return 文件的大小，以byte为单位
     * @throws IOException
     */
    public static long writeParamsToFile(String filePath, Map<String, String> params) throws IOException {
        File file = getFileByPath(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        for(String key : params.keySet()){
            bufferedWriter.write(key);
            bufferedWriter.write(" : ");
            bufferedWriter.write(params.get(key) == null ? " " : params.get(key));
            bufferedWriter.newLine();
        }

        bufferedWriter.close();
        fileWriter.close();

        return file.length();
    }

    /**
     * 将流中的信息指定的行数写入对应文件.写一行换一行
     * @param filePath      文件的路径
     * @param stream        需要写入文件的数据流
     * @param lines         需要写入文件的数据行数
     * @return 文件大小，以byte为单位
     * @throws IOException
     */
    public static long writeTextStreamToFile(String filePath, InputStream stream, int lines) throws IOException {
        File file = getFileByPath(filePath);
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while(lines > 0) {
            bufferedWriter.write(bufferedReader.readLine());
            bufferedWriter.newLine();
            lines--;
        }
        bufferedWriter.close();
        fileWriter.close();
        bufferedReader.close();
        inputStreamReader.close();

        return file.length();

    }

    /**
     * 根据文件路径获取文件
     * @param filePath  文件路径
     * @return 文件类
     * @throws IOException
     */
    public static File getFileByPath(String filePath) throws IOException {
        File file = new File(filePath);
        if(file.exists() == false) {
            file.createNewFile();
        }
        return file;
    }

    public static BufferedWriter getBufferedWriterByPath(String filePath) throws IOException {
        File file = getFileByPath(filePath);
        FileWriter fileWriter = new FileWriter(file,false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        return bufferedWriter;
    }

    public static BufferedReader getBufferedReaderByPath(String filePath) throws IOException {
        File file = getFileByPath(filePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        return bufferedReader;
    }

    public static long getFileSizeByPath(String filePath) throws IOException {
        File file = getFileByPath(filePath);
        return file.length();
    }

}
