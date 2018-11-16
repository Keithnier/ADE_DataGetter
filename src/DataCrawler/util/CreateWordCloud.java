package DataCrawler.util;



public class CreateWordCloud {
    public static void getWordCloud(){
        try {
            //设置命令行传入参数
            String[] args = new String[] { "python", "E:\\demo-python-wordcloud-master\\CreateWC.py","E:\\demo-python-wordcloud-master\\yes-minister.txt"};
            Process pr = Runtime.getRuntime().exec(args);
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        getWordCloud();
    }
}
