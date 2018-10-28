package IRTree.spatialindex.rtree;

import IRTree.query.Query;
import IRTree.spatialindex.spatialindex.Point;
import IRTree.spatialindex.spatialindex.Region;
import IRTree.spatialindex.storagemanager.*;
import util.Constants;
import util._readData;
import IRTree.spatialindex.rtree.Location;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static IRTree.regressiontest.TimeAreaMerge.timeAreaMerge1;

public class DataGetter_IRTree {
    private static Map<String, IRTree> timeIndex = new HashMap<>();
    private static double totalTime = 0;
    private static int totalNode = 0;
    private static int totalIO = 0;
    private static double queryTime = 0;
    private static double Utilization = 0;
    private static int cnt = 0; //记录查询query次数

    public static boolean build(String docsFileName, String btreeName, int fanout, int buffersize, boolean isCreate) throws Exception {
        // 1.BTree管理docs
        if(docsFileName.endsWith(".txt")) {
            docsFileName = Constants.DATA_DIRECTORY + File.separator + docsFileName;
            File docs = new File(docsFileName);
            if (!docs.exists()) {
                System.out.println("Data file is not exist!");
                return false;
            }
        }
        BtreeStore bs = BtreeStore.process(docsFileName, btreeName, isCreate);

        // 2.时间划分数据
        Map<String, List<String>> map = timeAreaMerge1(docsFileName);


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

//              DataCoordinate coordinates = pretreatment(time, x1, y1, x2, y2, maxTime, minDistance, x0, y0, t0, 0.5);// 归一化 和 数据映射
                    DataCoordinate coordinates = new DataCoordinate(time, x1, y1, x2, y2);
                    f1[0] = coordinates.x1;
                    f2[0] = coordinates.x2;
                    f1[1] = coordinates.y1;
                    f2[1] = coordinates.y2;

                    Region region = new Region(f1, f2);

                    byte[] data = new byte[100];

                    irTree.insertData(data, region, docId);
                    count++;
                    if (count % 10000 == 0) System.out.println(count);
                }
                irTree.buildInvertedIndex(bs);

                long end = System.currentTimeMillis();
                System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

                boolean ret = irTree.isIndexValid();
                if (ret == false) System.err.println("Structure is INVALID!");
                System.err.print(entry.getKey() + ":\n" + irTree + "\n\n");
            }
            timeIndex.put(entry.getKey(), irTree);
        }

        //再构建一棵totalIRTree，不分时间，将所有文档都保存在一棵树中
        System.out.println("totalIRTree is building!");
        PropertySet ps = new PropertySet();
        // .idx，.dat文件将被创建
        ps.setProperty("FileName", "totalIRTree");
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
        if (!isCreate)
            ps.setProperty("IndexIdentifier", 1);
        long start = System.currentTimeMillis();
        IRTree totalIRTree = new IRTree(ps, file, isCreate);

        if (isCreate) {
            for (Map.Entry<String, List<String>> entry : map.entrySet()) {
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

//              DataCoordinate coordinates = pretreatment(time, x1, y1, x2, y2, maxTime, minDistance, x0, y0, t0, 0.5);// 归一化 和 数据映射
                    DataCoordinate coordinates = new DataCoordinate(time, x1, y1, x2, y2);
                    f1[0] = coordinates.x1;
                    f2[0] = coordinates.x2;
                    f1[1] = coordinates.y1;
                    f2[1] = coordinates.y2;
//                    f1[2] = f2[2] = coordinates.time;
                    Region region = new Region(f1, f2);

                    byte[] data = new byte[100];

                    totalIRTree.insertData(data, region, docId);
                    count++;
                    if (count % 10000 == 0) System.out.println(count);
                }
                totalIRTree.buildInvertedIndex(bs);

                long end = System.currentTimeMillis();
//            System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

                totalTime += ((end - start) / 1000.0f);

                boolean ret = totalIRTree.isIndexValid();
                if (!ret) System.err.println("Structure is INVALID!");
                System.err.print("totalIRTree:\n" + totalIRTree + "\n\n");
            }
        }
        //将总树用也用timeIndex管理起来
        timeIndex.put("totalIRTree", totalIRTree);
        return true;
    }

    public static ArrayList<String> query(String time, String location, String key, int topk) throws Exception {
        String[] keys;

        if (!key.isEmpty() || !location.isEmpty()) {
            keys = key.split("\\s+"); //可划分一个或多个空格
        } else {
            return null;
        }
        //TODO:上面划分关键词应该采用分词器划分
        //通过字典获取关键字ID
        Vector<Integer> qwords;
        qwords = Query.findKeyId(keys, Constants.DATA_DIRECTORY + File.separator + "Dictionary.txt");

        Location loc = HttpUtil.getLocationByName(location);
        double[] f = loc.getCoordinate();
        Point qp = new Point(f);
        long start = System.currentTimeMillis();
        ArrayList<Integer> list;
        if (timeIndex.containsKey(time)) {
            IRTree irTree = timeIndex.get(time);
            list = irTree.Find_AllO_Rank_K(qwords, qp, topk, 0.5);
            if (list != null && list.size() > 0) {
                System.out.println(list);
            } else System.out.println("Nothing has been found");
        } else {
            //如果输入的时间不正确，就直接搜索所有记录
            IRTree irTree = timeIndex.get("totalIRTree");
            list = irTree.Find_AllO_Rank_K(qwords, qp, topk, 0.5);
            if (list != null && list.size() > 0) {
                System.out.println(list);
            } else System.out.println("Nothing has been found");
        }
        long end = System.currentTimeMillis();
        queryTime += (end - start) / 1000.0;
//        System.err.println("Query Time: " + queryTime + "s");
        cnt += 1;
        return DataGetter_IRTree.id2String(list);
    }


    public static void close() throws Exception {
        totalTime += queryTime;
        queryTime /= cnt;
        cnt = 0;
        for (Map.Entry<String, IRTree> entry : timeIndex.entrySet()) {
            // 等待前面的数据完全输出,保证数据写入文件
            TimeUnit.SECONDS.sleep(1);
//            System.err.println(entry.getKey() + ":\n" + entry.getValue());
            totalIO += entry.getValue().getIO() + entry.getValue().m_stats.getWrites();
            totalNode += entry.getValue().m_stats.getNumberOfNodes();
            IRTree irTree = timeIndex.get(entry.getKey());
            Utilization += 100 * irTree.m_stats.getNumberOfData() /
                    (irTree.m_stats.getNumberOfNodesInLevel(0) * irTree.m_leafCapacity);
            cnt += 1;
            entry.getValue().close();
        }

        System.err.println("totalIO: " + totalIO);
        System.err.println("totalNode: " + totalNode);
        System.err.println("totalTime: " + totalTime + "s");
        System.err.println("queryTime: " + queryTime + "s");//平均匹配时间
        System.err.println("Utilization: " + Utilization / cnt + "%");//资源利用率
    }

    private static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static ArrayList<String> id2String(ArrayList<Integer> list){
        if (list==null) return null;
        ArrayList<String> result = new ArrayList<>();
        for (Integer key : list){
                result.add(_readData.idMap.get(key));
        }
        return result;
    }

}
