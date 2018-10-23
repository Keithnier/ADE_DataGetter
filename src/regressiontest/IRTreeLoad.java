package regressiontest;

import spatialindex.rtree.IRTree;
import spatialindex.spatialindex.*;
import spatialindex.storagemanager.*;

import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Scanner;
import java.util.StringTokenizer;

public class IRTreeLoad {
    public static void main(String[] args) {
        System.out.println("Load Start!!");
        System.out.println("请依次输入: input_file tree_file capacity query_type invertedIndex_filename");
        Scanner in = new Scanner(System.in);
        String input = in.nextLine();
        String[] inputList = input.split(" ");
        new IRTreeLoad(inputList);
    }

    IRTreeLoad(String[] inputList) {
        try {

            LineNumberReader lr = null;

            lr = new LineNumberReader(new FileReader(inputList[0]));

            // 构建存储管理器属性
            PropertySet ps = new PropertySet();
            // 如果文件存在，则重写
            ps.setProperty("Overwrite", new Boolean(true));
            // 文件名 添加.idx文件和.dat文件
            ps.setProperty("FileName", inputList[1]);
            // 配置页面大小
            ps.setProperty("PageSize", new Integer(4096));

            // 选择磁盘存储管理器
            IStorageManager diskfile = new DiskStorageManager(ps);

            // 从内存或磁盘得到缓存区
            IBuffer file = new RandomEvictionsBuffer(diskfile, 10, false);

            // 配置IRTree索引
            PropertySet ps2 = new PropertySet();
            // 使用RTree的通用配置，倒排索引单独配置
            ps2.setProperty("BufferSize", new Integer(4096));
            ps2.setProperty("PageSize", new Integer(4096));
            ps2.setProperty("FileName", new String(inputList[4]));

            // 构建IRTree
            ISpatialIndex tree = new IRTree(ps2, file, false);

            // 测试
            int id, op;
            int count = 0;
            double x1, x2, y1, y2;
            double[] f1 = new double[2];
            double[] f2 = new double[2];

            long start = System.currentTimeMillis();
            String line = lr.readLine();

            while(line != null) {
                StringTokenizer st = new StringTokenizer(line);
                op = new Integer(st.nextToken()).intValue();
                id = new Integer(st.nextToken()).intValue();
                x1 = new Double(st.nextToken()).doubleValue();
                y1 = new Double(st.nextToken()).doubleValue();
                x2 = new Double(st.nextToken()).doubleValue();
                y2 = new Double(st.nextToken()).doubleValue();

                if (op == 0)
                {
                    //delete

                    f1[0] = x1; f1[1] = y1;
                    f2[0] = x2; f2[1] = y2;
                    Region r = new Region(f1, f2);

                    if (tree.deleteData(r, id) == false)
                    {
                        System.err.println("Cannot delete id: " + id + " , count: " + count + ".");
                        System.exit(-1);
                    }
                }
                else if (op == 1)
                {
                    //insert

                    f1[0] = x1; f1[1] = y1;
                    f2[0] = x2; f2[1] = y2;
                    Region r = new Region(f1, f2);

                    String data = r.toString();

                    //将数据存在数据库或者外存，内存中存为null
                    tree.insertData(null, r, id);
                    // example of passing a null pointer as the associated data.
                }
                else if (op == 2)
                {
                    //query

                    f1[0] = x1; f1[1] = y1;
                    f2[0] = x2; f2[1] = y2;

                    MyVisitor vis = new MyVisitor();

                    if (inputList[3].equals("intersection"))
                    {
                        Region r = new Region(f1, f2);
                        tree.intersectionQuery(r, vis);
                        // this will find all data that intersect with the query range.
                    }
                    else if (inputList[3].equals("10NN"))
                    {
                        Point p = new Point(f1);
                        tree.nearestNeighborQuery(10, p, vis);
                        // this will find the 10 nearest neighbors.
                    }
                    else
                    {
                        System.err.println("Unknown query type.");
                        System.exit(-1);
                    }
                }

                if ((count % 1000) == 0) System.err.println(count);

                count++;
                line = lr.readLine();
            }

            long end = System.currentTimeMillis();

            System.err.println("Operations: " + count);
            System.err.println(tree);
            System.err.println("Minutes: " + ((end - start) / 1000.0f) / 60.0f);

            // since we created a new RTree, the PropertySet that was used to initialize the structure
            // now contains the IndexIdentifier property, which can be used later to reuse the index.
            // (Remember that multiple indices may reside in the same storage manager at the same time
            //  and every one is accessed using its unique IndexIdentifier).
            Integer indexID = (Integer) ps2.getProperty("IndexIdentifier");
            System.err.println("Index ID: " + indexID);

            boolean ret = tree.isIndexValid();
            if (ret == false) System.err.println("Structure is INVALID!");

            // flush all pending changes to persistent storage (needed since Java might not call finalize when JVM exits).
            tree.flush();
            ((IRTree)tree).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }// IRTreeLoad

    class MyVisitor implements IVisitor
    {
        public void visitNode(final INode n) {}

        public void visitData(final IData d)
        {
            System.out.println(d.getIdentifier());
            // the ID of this data entry is an answer to the query. I will just print it to stdout.
        }
    }
}
