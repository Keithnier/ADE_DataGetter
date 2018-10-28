package IRTree.regressiontest;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DataGenerator {
    public static void main(String[] args) throws IOException {
        System.out.println("Usage: fileName(数据文件名) docNum(文档数目) wordRange(关键词id范围)");
        Scanner in = new Scanner(System.in);
        String line = in.nextLine();
        while(line == null || line.equals(""))
            line = in.nextLine();
        String[] params = line.split(" ");
        String filepath = System.getProperty("user.dir") + File.separator + "src" +
                File.separator + "regressiontest" + File.separator + "test3" + File.separator + params[0] + ".gz";
        File file = new File(filepath);
        if(file.exists()) {
            file.delete();
        }
        file.createNewFile();
        GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(file));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(gos)));
        int docNum = Integer.parseInt(params[1]);
        int wordRange = Integer.parseInt(params[2]);
        Random rand = new Random();
        for(int i = 0; i < docNum; i++) {
            StringBuilder line1 = new StringBuilder();
            line1.append(String.valueOf(i)); // id
            line1.append(",");
            line1.append(String.valueOf(rand.nextFloat())); // time
            line1.append(",");
            line1.append(String.valueOf(rand.nextFloat())); // x1
            line1.append(",");
            line1.append(String.valueOf(rand.nextFloat())); // y1
            line1.append(",");
            line1.append(String.valueOf(rand.nextFloat())); // x2
            line1.append(",");
            line1.append(String.valueOf(rand.nextFloat())); // y2
            line1.append(",");
            int wordNum = rand.nextInt(20);
            for(int j = 0; j < wordNum; j++) {
                line1.append(String.valueOf(rand.nextInt(wordRange)));
                line1.append(" ");
                float f = rand.nextFloat();
                line1.append(String.valueOf(rand.nextFloat()));
                line1.append(",");
            }
            pw.println(line1.toString());
        }
        pw.close();
    }

    public static void displayData() throws IOException {
        System.out.println("请输入文件名:");
        Scanner in = new Scanner(System.in);
        String tmp = in.nextLine();
        String filepath = System.getProperty("user.dir") + File.separator + "src" +
                File.separator + "regressiontest" + File.separator + "test3" + File.separator + tmp + ".gz";
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new GZIPInputStream(new FileInputStream(filepath))
        ));
        String line;
        int counter = 0;
        while((line = reader.readLine()) != null && (counter < 5000)) {
            System.out.println(counter++ + ":\t" +line);
        }
        reader.close();
    }
}
