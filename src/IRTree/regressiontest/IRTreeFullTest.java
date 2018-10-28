package IRTree.regressiontest;

import IRTree.spatialindex.rtree.IRTree;

public class IRTreeFullTest {
//    public static void main(String[] args) throws Exception {
//        if (args.length != 4)
//        {
//            System.err.println("Usage: IRTree docstore tree_file fanout buffersize.");
//            System.exit(-1);
//        }
//        String docfile = args[0];
//        String treefile = args[1];
//        int fanout = Integer.parseInt(args[2]);
//        int buffersize = Integer.parseInt(args[3]);
//
//        // 输入文档管理
//        BtreeStore docstore = new BtreeStore(docfile, false);
//        // 配置属性
//        // Create a disk based storage manager.
//        PropertySet ps = new PropertySet();
//
//        ps.setProperty("FileName", treefile);
//        // .idx and .dat extensions will be added.
//
//        Integer i = new Integer(4096*fanout/100);
//        ps.setProperty("PageSize", i);
//        // specify the page size. Since the index may also contain user defined data
//        // there is no way to know how big a single node may become. The storage manager
//        // will use multiple pages per node if needed. Off course this will slow down performance.
//
//        ps.setProperty("BufferSize", buffersize);
//
//        // 索引文件管理
//        IStorageManager diskfile = new DiskStorageManager(ps);
//
//        IBuffer file = new TreeLRUBuffer(diskfile, buffersize, false);
//        // applies a main memory random buffer on top of the persistent storage manager
//        // (LRU buffer, etc can be created the same way).
//        // 构建IRTree
//        IRTree irtree = new IRTree(ps, file, false);
//
//
//        long start = System.currentTimeMillis();
//
//        irtree.build("src/regressiontest/test3/dataOfBtree.gz", treefile, fanout, buffersize);
//        irtree.buildInvertedIndex(docstore);
//
//        long end = System.currentTimeMillis();
//        boolean ret = irtree.isIndexValid();
//        if (ret == false) System.err.println("Structure is INVALID!");
//        irtree.close();
//
//        System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);
//
//    }

    public static void main(String[] args) throws Exception {
        if (args.length != 5) {
            System.err.println("Usage: IRTree docsFileName btreeName indexFileName fanout buffersize.");
            System.exit(-1);
        }
        String docsFileName = args[0];
//        System.out.println(docsFileName);
        String btreeName = args[1];
        String indexFileName = args[2];
        int fanout = Integer.parseInt(args[3]);
        int buffersize = Integer.parseInt(args[4]);
        // 首次使用，boolean值为true
        IRTree.build(docsFileName, btreeName, indexFileName, fanout, buffersize, false);
    }
}
