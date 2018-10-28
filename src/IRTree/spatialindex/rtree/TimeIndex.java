package IRTree.spatialindex.rtree;

import IRTree.regressiontest.TimeAreaMerge;
import IRTree.spatialindex.spatialindex.Point;
import IRTree.spatialindex.spatialindex.Region;
import IRTree.spatialindex.storagemanager.*;
import util.Constants;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static IRTree.regressiontest.TimeAreaMerge.timeAreaMerge1;
import static IRTree.regressiontest.TimeAreaMerge.timeChange;

public class TimeIndex {
    private static Map<String, IRTree> timeIndex = new HashMap<>();

    /**
     * 1.创建时间Map。
     * 2.对于每一个时间，建立以个IRTree
     * 3.保存索引
     */
    public static void build(String docsFileName, String btreeName, int fanout, int buffersize, boolean isCreate) throws Exception {
        // 1.BTree管理docs
        docsFileName = Constants.DATA_DIRECTORY + File.separator + docsFileName;
        BtreeStore bs = BtreeStore.process(docsFileName, btreeName, isCreate);
//        BtreeStore bs = BtreeStore.process(docsFileName, btreeName, false);

        // 2.时间划分数据
        Map<String, List<String>> map = timeAreaMerge1(docsFileName);
        double totalTime = 0;
        int totalNode = 0;
        int totalIO = 0;
        double queryTime = 0;

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            String indexFileName = btreeName.substring(0, btreeName.indexOf("b")) + entry.getKey();
            PropertySet ps = new PropertySet();

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
            ps.setProperty("Dimension", 2);

//            // 计算最大时间间隔 和 容纳所有对象的最小矩形边长
//            double minDistance = 0;
//            double maxTime = 0;
//            double x0 = 0, y0 = 0, t0 = 0;
//
//            if (isCreate) {
//                String[] temp;
//                float time = 0, x1 = 0, y1 = 0, x2, y2;
//                double[] f1 = new double[3];
//                double[] f2 = new double[3];
//
//                for (String line : entry.getValue()) {
//                    temp = line.split(",");
//                    time = Float.parseFloat(temp[1]);
//                    x1 = Float.parseFloat(temp[2]);
//                    y1 = Float.parseFloat(temp[3]);
//                    x2 = Float.parseFloat(temp[4]);
//                    y2 = Float.parseFloat(temp[5]);
//
//                    maxTime = Math.max(maxTime, time);
//                    minDistance = Math.max(minDistance, x2 - x1);
//                    minDistance = Math.max(minDistance, y2 - y1);
//
//                    x0 = Math.min(x0, x1);
//                    x0 = Math.min(x0, x2);
//                    y0 = Math.min(y0, y1);
//                    y0 = Math.min(y0, y2);
//                    t0 = Math.min(t0, time);
//                }
//                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(
//                        new FileOutputStream(Constants.PROPERTY_DIRECTORY + File.separator + entry.getKey() + ".properties")
//                ));
//                // 保存计算中间结果到配置文件，后续查询不需要再次计算。
//                out.writeDouble(x0);
//                out.writeDouble(y0);
//                out.writeDouble(t0);
//                out.writeDouble(minDistance);
//                out.writeDouble(maxTime);
//                out.flush();
//                out.close();
//            } else {
//                DataInputStream in = new DataInputStream(new BufferedInputStream(
//                        new FileInputStream(Constants.PROPERTY_DIRECTORY + File.separator + entry.getKey() + ".properties")));
//                x0 = in.readDouble();
//                y0 = in.readDouble();
//                t0 = in.readDouble();
//                minDistance = in.readDouble();
//                maxTime = in.readDouble();
//                in.close();
//            }

            // 如果.idx文件已经建立，该值为m_header的页号
            if (!isCreate)
                ps.setProperty("IndexIdentifier", 1);
            long start = System.currentTimeMillis();
            IRTree irTree = new IRTree(ps, file, isCreate);

            if (isCreate) {
                String[] temp;
                int count = 0;
                double[] f1 = new double[2];
                double[] f2 = new double[2];
                for (String line : entry.getValue()) {
                    temp = line.split(",");
                    int docId = Integer.parseInt(temp[0]);
                    float time = Float.parseFloat(temp[1]);
                    float x1 = Float.parseFloat(temp[2]);
                    float y1 = Float.parseFloat(temp[3]);
                    float x2 = Float.parseFloat(temp[4]);
                    float y2 = Float.parseFloat(temp[5]);

//                    DataCoordinate coordinates = pretreatment(time, x1, y1, x2, y2, maxTime, minDistance, x0, y0, t0, 0.5);// 归一化 和 数据映射
                    DataCoordinate coordinates = new DataCoordinate(time, x1, y1, x2, y2);
                    f1[0] = coordinates.x1;
                    f2[0] = coordinates.x2;
                    f1[1] = coordinates.y1;
                    f2[1] = coordinates.y2;
//                    f1[2] = f2[2] = coordinates.time;
                    Region region = new Region(f1, f2);

                    byte[] data = new byte[100];

                    irTree.insertData(data, region, docId);

                    count++;
                    if (count % 10000 == 0) System.out.println(count);
                }
                irTree.buildInvertedIndex(bs);

                long end = System.currentTimeMillis();
                System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

                totalTime += ((end - start) / 1000.0f) / 60.0f;

                boolean ret = irTree.isIndexValid();
                if (ret == false) System.err.println("Structure is INVALID!");
                System.err.print(entry.getKey() + ":\n" + irTree + "\n\n");
            }
            timeIndex.put(entry.getKey(), irTree);
        }

        // Query
        Vector qwords = new Vector();
        int k = 10;
        qwords.add(19);
        qwords.add(23);
//        qwords.add(23);
//        qwords.add(1234);
        String time = "1523491609241";
//        DataCoordinate coordinates = new DataCoordinate(0, -85.605166, 30.355644, -80.742567, 35.000771);
        DataCoordinate coordinates = new DataCoordinate(0, -85.605166, 30.355644, -80.742567, 35.000771);

        double[] f = new double[2];
        f[0] = (coordinates.x1 + coordinates.x2) / 2;
        f[1] = (coordinates.y1 + coordinates.y2) / 2;
        Point qp = new Point(f);
        long start = System.currentTimeMillis();
        if (timeIndex.containsKey(timeChange(time, TimeAreaMerge.TYPE))) {
            IRTree irTree = timeIndex.get(timeChange(time, TimeAreaMerge.TYPE));
            ArrayList<Integer> list = irTree.Find_AllO_Rank_K(qwords, qp, k, 0.5);
            if (list != null && list.size() > 0)
                System.out.println(list);
            else System.out.println("Nothing has been found");
        } else {
            System.out.println("Time Error: " + timeChange(time, TimeAreaMerge.TYPE));
        }
        long end = System.currentTimeMillis();
        System.err.println("Query Time: " + (end - start) / 1000.0 + "s");
        queryTime += (end - start) / 1000.0;
        // End of Query

        for (Map.Entry<String, IRTree> entry : timeIndex.entrySet()) {
            // 等待前面的数据完全输出
            TimeUnit.SECONDS.sleep(1);
//            System.err.println(entry.getKey() + ":\n" + entry.getValue());
            totalIO += entry.getValue().getIO() + entry.getValue().m_stats.getWrites();
            totalNode += entry.getValue().m_stats.getNumberOfNodes();
            entry.getValue().close();
        }
        System.err.println("totalIO: " + totalIO);
        System.err.println("totalNode: " + totalNode);
        System.err.println("totalTime: " + totalTime);
        System.err.println("queryTime: " + queryTime);
    }
}
