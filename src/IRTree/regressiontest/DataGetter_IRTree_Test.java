package IRTree.regressiontest;

import IRTree.spatialindex.rtree.DataGetter_IRTree;

import java.util.ArrayList;

public class DataGetter_IRTree_Test {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Usage: IRTree docsFileName btreeName fanout buffersize.");
            System.exit(-1);
        }
        String docsFileName = args[0];
        String btreeName = args[1];
        int fanout = Integer.parseInt(args[2]);
        int buffersize = Integer.parseInt(args[3]);
        // 首次使用，boolean值为true
        DataGetter_IRTree.build(docsFileName, btreeName, fanout, buffersize, false);
        System.out.println("2018-07-09");
        ArrayList<String> result = DataGetter_IRTree.query("2018-07-19","   华盛顿 ","  buy lol", 10);
        System.out.println(result);
        System.out.println("2018-07-19");
        result = DataGetter_IRTree.query("2018-07-19","   华盛顿 ","  buy lol", 10);
        System.out.println(result);
        System.out.println("缺省时间");
        result = DataGetter_IRTree.query("2018-07-19","   华盛顿 ","  buy lol", 10);
        System.out.println(result);
        DataGetter_IRTree.close();
    }
}
