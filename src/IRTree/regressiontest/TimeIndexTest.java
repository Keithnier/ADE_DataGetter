package IRTree.regressiontest;

import IRTree.spatialindex.rtree.TimeIndex;

public class TimeIndexTest {
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
        TimeIndex.build(docsFileName, btreeName, fanout, buffersize, true);
    }
}
