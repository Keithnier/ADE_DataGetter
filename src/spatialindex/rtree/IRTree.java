package spatialindex.rtree;

import invertedindex.InvertedIndex;
import neustore.base.FloatData;
import neustore.base.LRUBuffer;
import query.Query;
import spatialindex.spatialindex.*;
import spatialindex.storagemanager.*;
import util.Constants;

import java.io.*;
import java.util.*;

public class IRTree extends RTree {

    //protected  Hashtable numberofpoint = new Hashtable();

    protected InvertedIndex iindex;
    protected int count;


    public IRTree(PropertySet ps, IStorageManager sm, boolean isCreate) throws IOException {
        super(ps, sm);
        int buffersize = (Integer) ps.getProperty("BufferSize");
        int pagesize = (Integer) ps.getProperty("PageSize");
        String file = (String) ps.getProperty("FileName");
        LRUBuffer buffer = new LRUBuffer(buffersize, pagesize);
        iindex = new InvertedIndex(buffer, Constants.SAVEDATA_DIRECTORY +  File.separator
                + file + ".iindex", isCreate, pagesize, buffersize);
    }

    public void buildInvertedIndex(BtreeStore docstore) throws Exception {
        count = 0;
        Node n = readNode(m_rootID);
//        System.err.println(m_rootID);
        post_traversal_iindex(n, docstore);
    }

    private Vector post_traversal_iindex(Node n, BtreeStore docstore) throws Exception {
        if (n.isLeaf()) {
            Hashtable invertedindex = new Hashtable();
            n.numOfleaf = n.m_children;
            writeNode(n);
            iindex.create(n.m_identifier);
            int child;
            for (child = 0; child < n.m_children; child++) {
                int docID = n.m_pIdentifier[child];
                Vector document = docstore.getDoc(docID);
                if (document == null) {
                    System.out.println("Can't find document " + docID);
                    System.exit(-1);
                }
                iindex.insertDocument(docID, document, invertedindex);
            }
            return iindex.store(n.m_identifier, invertedindex, n.m_children);
        } else {
            Hashtable invertedindex = new Hashtable();
            iindex.create(n.m_identifier);
            System.out.println("processing index node " + n.m_identifier);
            System.out.println("level " + n.m_level);
            int child;
            for (child = 0; child < n.m_children; child++) {
                Node nn = readNode(n.m_pIdentifier[child]);
                Vector pseudoDoc = post_traversal_iindex(nn, docstore);
                n.numOfleaf = n.numOfleaf + nn.numOfleaf;
                int docID = n.m_pIdentifier[child];
                iindex.insertDocument(docID, pseudoDoc, invertedindex);
                count++;
                System.out.println(count + "/" + m_stats.getNumberOfNodes());
            }
            writeNode(n);
            return iindex.store(n.m_identifier, invertedindex, n.m_children);
        }
    }


    public void close() throws IOException {
        flush();
        iindex.close();
    }

    /**
     * 查询流程：
     * 1.从根结点开始，查询倒排索引，找到包含查询关键字的文档id
     * 2.对于所有的子节点，如果包含候选文档id，那么就计算一个得分，存入优先级队列，否则跳过
     * 3.从优先级队列中取出得分最高的结点，如果是内部结点，继续上述过程，否则是外部结点，则其为所求文档
     *      *
     *      * @param qwords 查询关键字集合
     *      * @param qpoint 查询点坐标
     *      * @param topk   查询返回结果个数
     *      * @param alpha  空间和文本权重调节系数
     *      * @return 返回查询结果
     *      * @throws Exception
     */
    public ArrayList<Integer> Find_AllO_Rank_K(Vector qwords, Point qpoint, int topk, double alpha) throws Exception {

        PriorityQueue<NNEntry> queue = new PriorityQueue<NNEntry>(100, new NNEntryComparator());
        NNComparator nnc = new NNComparator();
        double knearest = Double.MIN_VALUE;

        double ooo = Math.sqrt(2);
        int count = 0;
        int object_id = 0;
        ArrayList<Integer> line = new ArrayList<Integer>();
        // 根结点
        Node n = null;
        Data nd = new Data(null, null, m_rootID);

        queue.add(new NNEntry(nd, 0.0, 4));

        while (queue.size() != 0) {
            NNEntry first = (NNEntry) queue.poll();

            if (count >= topk && first.m_minDist < knearest) break;

            //内部结点
            if (first.level > 0) {
                IData fd = (IData) first.m_pEntry;
                n = readNode(fd.getIdentifier());

                iindex.load(n.m_identifier);

                // 由查询关键字得到所有候选文档
                Hashtable trscore = iindex.textRelevancy(qwords);


                for (int cChild = 0; cChild < n.m_children; cChild++) {
                    Object var = trscore.get(n.m_pIdentifier[cChild]);
                    //////////////////////////////
                    if (var == null) {
                        continue;
                    }
                    FloatData trs = (FloatData) var;
                    trs.data = trs.data / InvertedIndex.maxTR;
                    trs.data2 = trs.data2 / InvertedIndex.maxTR;

                    //////////////////////////////
                    IEntry e = new Data(n.m_pMBR[cChild], n.m_pIdentifier[cChild]);
                    double dist = nnc.getMinimumDistance(qpoint, e) / ooo;
                    double score = (1 - alpha) * (1 - dist) + alpha * trs.data;
                    NNEntry e2 = new NNEntry(e, score, n.m_level, 1 - dist, trs.data);
                    queue.add(e2);
                }
            } else {    //MJL Algorithm 4: if  is a leaf node then
                //System.out.println(first.m_pEntry.getIdentifier() + ":" + first.m_minDist);
                object_id = first.m_pEntry.getIdentifier();
                count++;//???
                knearest = first.m_minDist;
//                if (count > 10) line.add(object_id);
                line.add(object_id);
                System.err.println(String.format("id %d\tscore %f", first.m_pEntry.getIdentifier(), first.m_minDist));
            }
        }


        if (line.size() > 0) {
            m_stats.m_queryResults = line.size();
            return line;
        }
        return null;
    }

    /**
     * @param qWords             关键字字符形式
     * @param qPoint
     * @param topk
     * @param alpha
     * @param dictionaryFilePath
     * @return
     * @throws Exception
     * @description 使用字符形式的关键字查询
     * @author Pulin Xie
     */
    public ArrayList<Integer> findTopK(String[] qWords, Point qPoint, int topk, double alpha, String dictionaryFilePath) throws Exception {
        Vector<Integer> keysId = Query.findKeyId(qWords, dictionaryFilePath);
        return Find_AllO_Rank_K(keysId, qPoint, topk, alpha);
    }

    public int Find_O_Rank_K(Vector qwords, Point qpoint, int topk, double alpha) throws Exception {

        PriorityQueue<NNEntry> queue = new PriorityQueue<NNEntry>(100, new NNEntryComparator());
        NNComparator nnc = new NNComparator();
        double ooo = Math.sqrt(2);
        int count = 0;
        int object_id = 0;

        Node n = null;
        Data nd = new Data(null, null, m_rootID);

        queue.add(new NNEntry(nd, 0.0, 4));

        while (queue.size() != 0) {
            NNEntry first = (NNEntry) queue.poll();

            if (first.level > 0) {
                IData fd = (IData) first.m_pEntry;
                n = readNode(fd.getIdentifier());

                iindex.load(n.m_identifier);
                Hashtable trscore = iindex.textRelevancy(qwords);


                for (int cChild = 0; cChild < n.m_children; cChild++) {
                    Object var = trscore.get(n.m_pIdentifier[cChild]);
                    //////////////////////////////
                    if (var == null) {

                        continue;
                    }
                    FloatData trs = (FloatData) var;
                    trs.data = trs.data / InvertedIndex.maxTR;
                    trs.data2 = trs.data2 / InvertedIndex.maxTR;

                    //////////////////////////////
                    IEntry e = new Data(n.m_pMBR[cChild], n.m_pIdentifier[cChild]);
                    double dist = nnc.getMinimumDistance(qpoint, e) / ooo;
                    double score = (1 - alpha) * (1 - dist) + alpha * trs.data;
                    NNEntry e2 = new NNEntry(e, score, n.m_level, 1 - dist, trs.data);
                    queue.add(e2);

                }
            } else {
                //System.out.println(first.m_pEntry.getIdentifier() + ":" + first.m_minDist);
                object_id = first.m_pEntry.getIdentifier();
                count++;
                if (count == topk) return object_id;
            }
        }

        if (count == topk) return object_id;
        else return -1;
    }

    public long getIO() {
        return m_stats.getReads() + iindex.buffer.getIOs()[0];
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Usage: IRTree docstore tree_file fanout buffersize.");
            System.exit(-1);
        }
        String docfile = args[0];
        String treefile = args[1];
        int fanout = Integer.parseInt(args[2]);
        int buffersize = Integer.parseInt(args[3]);

        BtreeStore docstore = new BtreeStore(docfile, false);

        // Create a disk based storage manager.
        PropertySet ps = new PropertySet();

        ps.setProperty("FileName", treefile);
        // .idx and .dat extensions will be added.

        Integer i = new Integer(4096 * fanout / 100);
        ps.setProperty("PageSize", i);
        // specify the page size. Since the index may also contain user defined data
        // there is no way to know how big a single node may become. The storage manager
        // will use multiple pages per node if needed. Off course this will slow down performance.

        ps.setProperty("BufferSize", buffersize);

        IStorageManager diskfile = new DiskStorageManager(ps);

        IBuffer file = new TreeLRUBuffer(diskfile, buffersize, false);
        // applies a main memory random buffer on top of the persistent storage manager
        // (LRU buffer, etc can be created the same way).

        i = new Integer(1); // INDEX_IDENTIFIER_GOES_HERE (suppose I know that in this case it is equal to 1);
        ps.setProperty("IndexIdentifier", i);

        IRTree irtree = new IRTree(ps, file, false);

        long start = System.currentTimeMillis();
//		irtree.build("src/regressiontest/test3/dataOfBtree.gz", "irtree", 100, 4096);
        irtree.buildInvertedIndex(docstore);

        long end = System.currentTimeMillis();
        boolean ret = irtree.isIndexValid();
        if (ret == false) System.err.println("Structure is INVALID!");
        irtree.close();

        System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);


    }

    double getMaximumDistance(Point qpoint, IEntry e) {
        IShape s = e.getShape();
        double ans = -1;

        if (s instanceof Point) {
            Point p = (Point) s;
            ans = Math.sqrt((p.m_pCoords[0] - qpoint.m_pCoords[0]) * (p.m_pCoords[0] - qpoint.m_pCoords[0]) +
                    (p.m_pCoords[1] - qpoint.m_pCoords[1]) * (p.m_pCoords[1] - qpoint.m_pCoords[1]));
        } else {
            Region r = (Region) s;
            double temp = 0.0;
            temp = Math.sqrt((r.m_pLow[0] - qpoint.m_pCoords[0]) * (r.m_pLow[0] - qpoint.m_pCoords[0]) +
                    (r.m_pLow[1] - qpoint.m_pCoords[1]) * (r.m_pLow[1] - qpoint.m_pCoords[1]));
            if (temp > ans) ans = temp;
            temp = Math.sqrt((r.m_pHigh[0] - qpoint.m_pCoords[0]) * (r.m_pHigh[0] - qpoint.m_pCoords[0]) +
                    (r.m_pHigh[1] - qpoint.m_pCoords[1]) * (r.m_pHigh[1] - qpoint.m_pCoords[1]));
            if (temp > ans) ans = temp;
            temp = Math.sqrt((r.m_pLow[0] - qpoint.m_pCoords[0]) * (r.m_pLow[0] - qpoint.m_pCoords[0]) +
                    (r.m_pHigh[1] - qpoint.m_pCoords[1]) * (r.m_pHigh[1] - qpoint.m_pCoords[1]));
            if (temp > ans) ans = temp;
            temp = Math.sqrt((r.m_pHigh[0] - qpoint.m_pCoords[0]) * (r.m_pHigh[0] - qpoint.m_pCoords[0]) +
                    (r.m_pLow[1] - qpoint.m_pCoords[1]) * (r.m_pLow[1] - qpoint.m_pCoords[1]));
            if (temp > ans) ans = temp;
        }

        return ans;
    }

    /**
     * 构建IRTree索引
     *
     * @param docsFileName
     * @param btreeName     B树的名字，如btree，用来管理文档
     * @param indexFileName 索引的名字，如irtree，索引文件
     * @param fanout
     * @param buffersize
     * @param isCreate
     * @throws Exception
     * @author Pulin Xie
     */
    public static void build(String docsFileName, String btreeName, String indexFileName, int fanout, int buffersize, boolean isCreate) throws Exception {
//        docsFileName = System.getProperty("user.dir") + File.separator + "src" +
//                File.separator + "regressiontest" + File.separator + "test3" + File.separator + docsFileName + ".gz";
//        BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(docsFileName))));
        docsFileName = docsFileName;
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(docsFileName)));
        /**
         * 1. 用BTree管理docs文件集
         * 2. 利用docs文件集构建RTree索引层
         * 3. 利用BTree的信息构建倒排索引
         */
        //1. BTree管理docs
//        BtreeStore bs = BtreeStore.process(docsFileName, btreeName, isCreate);
        BtreeStore bs = BtreeStore.process(Constants.DATA_DIRECTORY + File.separator + docsFileName, btreeName, isCreate);
        // 2. 构造索引层
        //索引文件管理器，磁盘
        PropertySet ps = new PropertySet();
        // .idx，.dat文件将被创建
        ps.setProperty("FileName", indexFileName);
        Integer pageSize = new Integer(4096 * fanout / 100);
        ps.setProperty("PageSize", pageSize);
        ps.setProperty("BufferSize", buffersize);
        ps.setProperty("Overwrite", isCreate);

        IStorageManager diskfile = new DiskStorageManager(ps);
        IBuffer file = new TreeLRUBuffer(diskfile, buffersize, false);

        ps.setProperty("FillFactor", 0.7);
        ps.setProperty("IndexCapacity", fanout);
        ps.setProperty("LeafCapacity", fanout);
        ps.setProperty("Dimension", 3);

        // 计算最大时间间隔 和 容纳所有对象的最小矩形边长
        double minDistance = 0;
        double maxTime = 0;
        double x0 = 0, y0 = 0, t0 = 0;
        if (isCreate) {
            String line;
            String[] temp;
            float time = 0, x1 = 0, y1 = 0, x2, y2;
            int count = 0;
            double[] f1 = new double[3];
            double[] f2 = new double[3];
            while ((line = reader.readLine()) != null) {
                temp = line.split(",");
                time = Float.parseFloat(temp[1]);
                x1 = Float.parseFloat(temp[2]);
                y1 = Float.parseFloat(temp[3]);
                x2 = Float.parseFloat(temp[4]);
                y2 = Float.parseFloat(temp[5]);

                maxTime = Math.max(maxTime, time);
                minDistance = Math.max(minDistance, x2 - x1);
                minDistance = Math.max(minDistance, y2 - y1);

                x0 = Math.min(x0, x1);
                x0 = Math.min(x0, x2);
                y0 = Math.min(y0, y1);
                y0 = Math.min(y0, y2);
                t0 = Math.min(t0, time);
            }
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(Constants.PROPERTY_DIRECTORY + File.separator + "properties")
            ));
            // 保存计算中间结果到配置文件，后续查询不需要再次计算。
            out.writeDouble(x0);
            out.writeDouble(y0);
            out.writeDouble(t0);
            out.writeDouble(minDistance);
            out.writeDouble(maxTime);
            out.flush();
            out.close();
            reader.close();
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(docsFileName)));
        } else {
            DataInputStream in = new DataInputStream(new BufferedInputStream(
                    new FileInputStream(Constants.PROPERTY_DIRECTORY + File.separator + "properties")));
            x0 = in.readDouble();
            y0 = in.readDouble();
            t0 = in.readDouble();
            minDistance = in.readDouble();
            maxTime = in.readDouble();
            in.close();
        }

        // 如果.idx文件已经建立，该值为m_header的页号
        if (!isCreate)
            ps.setProperty("IndexIdentifier", 1);
        long start = System.currentTimeMillis();
        IRTree irTree = new IRTree(ps, file, isCreate);

        if (isCreate) {
            String line;
            String[] temp;
            int count = 0;
            double[] f1 = new double[3];
            double[] f2 = new double[3];
            while ((line = reader.readLine()) != null) {
                temp = line.split(",");
                int docId = Integer.parseInt(temp[0]);
                float time = Float.parseFloat(temp[1]);
                float x1 = Float.parseFloat(temp[2]);
                float y1 = Float.parseFloat(temp[3]);
                float x2 = Float.parseFloat(temp[4]);
                float y2 = Float.parseFloat(temp[5]);
                // 归一化 和 数据映射
                DataCoordinate coordinates = pretreatment(time, x1, y1, x2, y2, maxTime, minDistance, x0, y0, t0, 0.5);
//                f1[0] = f2[0] = x;
//                f1[1] = f2[1] = y;
                f1[0] = coordinates.x1;
                f2[0] = coordinates.x2;
                f1[1] = coordinates.y1;
                f2[1] = coordinates.y2;
                f1[2] = f2[2] = coordinates.time;
                Region region = new Region(f1, f2);

                byte[] data = new byte[100];

                irTree.insertData(data, region, docId);

                count++;
                if (count % 10000 == 0) System.out.println(count);//MJL 10000->1000
            }
            irTree.buildInvertedIndex(bs);

            long end = System.currentTimeMillis();
            System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);
            boolean ret = irTree.isIndexValid();
            if (ret == false) System.err.println("Structure is INVALID!");
        }


        //Query
        Vector<Integer> qwords;
        Random rand = new Random();
        String[] keys = {
                "buy", "lol"
        };

        qwords = Query.findKeyId(keys, Constants.DATA_DIRECTORY + File.separator + "dic1_1.txt");

//        qwords.add(19);
//        qwords.add(1);
//        qwords.add(711);
//        qwords.add(719);
        double[] f = new double[3];
        DataCoordinate coordinates = pretreatment(1523491609241.0, -85.605166, 30.355644, -80.742567, 35.000771, maxTime, minDistance, x0, y0, t0, 0.5);
        f[0] = (coordinates.x1 + coordinates.x2) / 2;
        f[1] = (coordinates.y1 + coordinates.y2) / 2;
        f[2] = coordinates.time;
        Point qp = new Point(f);

        ArrayList<Integer> list = irTree.Find_AllO_Rank_K(qwords, qp, 7, 0.5);
        if (list != null && list.size() > 0)
            System.out.println(list);
        else System.out.println("Nothing has been found");
        System.err.println(irTree);
        System.out.println("totalIO: " + irTree.getIO() + "  " + irTree.m_stats.getWrites());
        irTree.close();
    }

    /**
     * 预处理的类，该类对数据进行预处理操作。
     *
     * @param time
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param T     给定的时间，默认设置的是给定数据集的最大时间
     * @param d     归一化处理的最大包含所有数据对象的最小矩形的边长
     * @param x0
     * @param y0    给定原点坐标，设置为真实数据中x,y的最小值
     * @param t0    给定时间，设置为给定时间的最小值
     * @param alpha 平滑系数
     * @return
     */
    public static DataCoordinate pretreatment(double time, double x1, double y1, double x2, double y2,
                                              double T, double d, double x0, double y0, double t0, double alpha) {
        assert (alpha >= 0 && alpha <= 1);
        // 归一化
        time = (time - t0) / T;
        x1 = (x1 - x0) / (Math.sqrt(2) * d);
        y1 = (y1 - y0) / (Math.sqrt(2) * d);
        x2 = (x2 - x0) / (Math.sqrt(2) * d);
        y2 = (y2 - y0) / (Math.sqrt(2) * d);

        // 数据映射
        x1 = alpha * Math.sqrt(2) * x1;
        y1 = alpha * Math.sqrt(2) * y1;
        x2 = alpha * Math.sqrt(2) * x2;
        y2 = alpha * Math.sqrt(2) * y2;
        time = (1 - alpha) * Math.sqrt(2) * time;

        return new DataCoordinate(time, x1, y1, x2, y2);
    }
}
