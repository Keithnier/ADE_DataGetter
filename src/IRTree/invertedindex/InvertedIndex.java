package IRTree.invertedindex;


import jdbm.RecordManagerFactory;
import jdbm.btree.BTree;
import jdbm.helper.ComparableComparator;
import jdbm.helper.DefaultSerializer;
import jdbm.recman.CacheRecordManager;
import IRTree.neustore.base.*;
import IRTree.neustore.heapfile.HeapFilePage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


public class InvertedIndex extends DBIndex {

    public static float maxTR = 5;

    protected CacheRecordManager cacheRecordManager;

    protected long          recid;

    protected BTree btree;

    protected int 		  pageSize;

    private int 		  numRecs;

    protected Key sampleKey;

    protected Data sampleData;

    protected Hashtable invertedlists;

    protected int count;

    public InvertedIndex(DBBuffer _buffer, String filename, boolean isCreate, int pagesize, int buffersize, Key _sampleKey, Data _sampleData) throws IOException {
        super( _buffer, filename, isCreate );
        sampleKey = _sampleKey;
        sampleData = _sampleData;

        int cachesize = buffersize;
        // RecordManagerFactory.createRecordManager()接受一个String作为参数，该参数作为它创建的两个数据库文件名字的前缀。
        cacheRecordManager = new CacheRecordManager(RecordManagerFactory.createRecordManager(filename), cachesize, true);
        pageSize = pagesize;
        /**
         * 如果是用RecordManager.insert()插入数据，数据存储形式为id+序列化的对象
         * 如果是BTree或HTree.insert()插入数据，数据保存形式为key+value，类似HashTable;
         */
        if ( !isCreate ) {
            // 这里是通过别名来取出数据的id，如果是有记录的id，那么就用fetch(id)的方法
            // BTree.load()方法也可以获得一个BTree的对象，该方法需要两个参数，一个是RecordManager，另一个是BTree的id值。
            // 而创建一个BTree对象使用的是静态的BTree.createInstance()，该方法接受两个参数，一个是Recordmanager，另一个是Comparator(让其能够对键值进行比较)
            recid = cacheRecordManager.getNamedObject( "0" );
            btree = BTree.load( cacheRecordManager, recid );
            //System.out.println("loading btree: " + btree.size());
        }
        else {
            btree = BTree.createInstance( cacheRecordManager, ComparableComparator.INSTANCE, DefaultSerializer.INSTANCE, DefaultSerializer.INSTANCE, 1000 );
            // jdbm中setNameObject()方法都是给对象起别名。同样getNameObject(别名)获得的都是该对象在jdbm中的id值。
            cacheRecordManager.setNamedObject( "0", btree.getRecid() );
        }

        invertedlists = new Hashtable();
        count = 0;
    }

    public InvertedIndex(DBBuffer _buffer, String filename, boolean isCreate, int pagesize, int buffersize)throws IOException{
        super( _buffer, filename, isCreate );
        sampleKey = new IntKey(0);
        sampleData = new FloatData(0);

        int cachesize = buffersize;
        cacheRecordManager = new CacheRecordManager(RecordManagerFactory.createRecordManager(filename), cachesize, true);

        pageSize = pagesize;

    }

    protected void readIndexHead (byte[] indexHead) {
        ByteArray ba = new ByteArray( indexHead, true );
        try {
            numRecs = ba.readInt();

        } catch (IOException e) {}
    }
    protected void writeIndexHead (byte[] indexHead) {
        ByteArray ba = new ByteArray( indexHead, false );
        try {
            ba.writeInt(numRecs);

        } catch (IOException e) {}
    }
    protected void initIndexHead() {
        numRecs = 0;
    }


    public int numRecs() { return numRecs; }

    protected HeapFilePage readPostingListPage(long pageID ) throws IOException {
        DBBufferReturnElement ret = buffer.readPage(file, pageID);
        HeapFilePage thePage = null;
        if ( ret.parsed ) {
            thePage = (HeapFilePage)ret.object;
        }
        else {
            thePage = new HeapFilePage(pageSize, sampleKey, sampleData);
            thePage.read((byte[])ret.object);
        }
        return thePage;
    }

    /**
     * 倒排索引结构：
     * wordID | (ArrayList<KeyData<docID,weight>>) data |....
     * wordID | (ArrayList<KeyData<docID,weight>>) data |....
     * @param docID
     * @param document
     * @param invertedindex
     * @throws IOException
     */

    public void insertDocument(int docID, Vector document, Hashtable invertedindex) throws IOException{
        IntKey key = new IntKey(docID);
        for(int i = 0; i < document.size(); i++){
            KeyData keydata = (KeyData)document.get(i);
            IntKey wordID = (IntKey)keydata.key;
            FloatData weight = (FloatData)keydata.data;
            KeyData rec = new KeyData(key, weight);
            if(invertedindex.containsKey(wordID.key)){
                ArrayList list = (ArrayList)invertedindex.get(wordID.key);
                list.add(rec);
            }
            else{
                ArrayList list = new ArrayList();
                list.add(rec);
                invertedindex.put(wordID.key, list);
            }
        }
    }

    public Vector store(int treeid, Hashtable invertedindex, int num) throws IOException{
        Vector pseudoDoc = new Vector();
        load(treeid);
        Iterator iter = invertedindex.keySet().iterator();
        while(iter.hasNext()){
            int wordID = (Integer)iter.next();
            ArrayList list = (ArrayList)invertedindex.get(wordID);
            long newPageID = allocate();
            Object var = btree.insert(wordID, newPageID, false);
            if(var != null){
                System.out.println("Btree insertion error: duplicate keys.");
                System.exit(-1);
            }
            HeapFilePage newPage = new HeapFilePage(pageSize, sampleKey, sampleData);

            FloatData weight = storelist(list, newPage, newPageID, num);
            IntKey key = new IntKey(wordID);
            KeyData rec = new KeyData(key, weight);
            pseudoDoc.add(rec);
        }
        cacheRecordManager.commit();
        return pseudoDoc;
    }


    public void commit()throws IOException{
        cacheRecordManager.commit();
    }

    private FloatData storelist(ArrayList list, HeapFilePage newPage, long newPageID, int num) throws IOException{
        FloatData weight = new FloatData(Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);

        for(int j = 0; j < list.size(); j++){
            KeyData rec = (KeyData)list.get(j);
            IntKey key = (IntKey)rec.key;
            FloatData data = (FloatData)rec.data;

            weight.data  = Math.max(weight.data, data.data);
            weight.data2 = Math.min(weight.data2, data.data2);

            int availableByte = newPage.getAvailableBytes();
            if(availableByte < key.size() + data.size()){

                long nextPageID = allocate();
                newPage.setNextPageID(nextPageID);
                buffer.writePage(file, newPageID, newPage);
                newPageID = nextPageID;
                newPage = new HeapFilePage(pageSize, sampleKey, sampleData);
            }
            newPage.insert(key, data);
        }
        if(list.size() < num) weight.data2 = 0;
        buffer.writePage(file, newPageID, newPage);
        return weight;
    }

    /**
     * 计算文本相似性得分
     * 1.对于每一关键字，查询倒排索引是否有该关键字
     * 2.如果没有，计算下一个关键字；如果有该关键字，将文档id存入hashtable
     * 3.如果HashTable中已经有该文档id，将其权重相加
     * @param qwords 关键字集合
     * @return
     * @throws IOException
     */
    public Hashtable textRelevancy(Vector qwords) throws IOException{

        Hashtable filter = new Hashtable();

        for(int j = 0; j < qwords.size(); j++){
            int word = (Integer)qwords.get(j);
            ArrayList list = readPostingList(word);
            for(int i = 0; i < list.size(); i++){
                KeyData rec = (KeyData)list.get(i);
                IntKey docID = (IntKey)rec.key;
                FloatData weight = (FloatData)rec.data;
                // 统计文档中所有查询关键字出现的次数，其权重和相加
                if(filter.containsKey(docID.key)){
                    FloatData w = (FloatData)filter.get(docID.key);
                    w.data  = w.data + weight.data;
                    w.data2 = w.data2+weight.data2;
                    filter.put(docID.key, w);
                }
                else
                    filter.put(docID.key, weight);
            }
        }
        return filter;
    }


    public ArrayList readPostingList(int wordID) throws IOException{
        ArrayList list = new ArrayList();
        Object var = btree.find(wordID);
        if(var == null){
            //System.out.println("Posting List not found " + wordID);
            //System.exit(-1);
            return list;
        }
        else{
            long firstPageID = (Long)var;

            while(firstPageID != -1){
                //System.out.println(" page " + firstPageID);
                HeapFilePage thePage = readPostingListPage(firstPageID);
                for(int i = 0; i < thePage.numRecs(); i++){
                    KeyData rec = thePage.get(i);
                    list.add(rec);
                }
                //System.out.println("size " + thePage.numRecs());
                firstPageID = thePage.getNextPageID();
            }

        }
        return list;
    }



    public int[] getIOs(){
        return buffer.getIOs();
    }



    public BTree getBtree(){
        return btree;
    }
    public void create(int treeid) throws IOException{

        String BTREE_NAME = String.valueOf(treeid);
//		System.out.println("BTREE_NAME " + BTREE_NAME);
        recid = cacheRecordManager.getNamedObject( BTREE_NAME );
//		System.out.println("true recid " + recid);
//		System.out.println(recid);
        if ( recid != 0 ) {
            cacheRecordManager.delete(recid);
//            System.out.println("Creating an existing btree: " + treeid);
//            System.exit(-1);
        }
//        else {
//            btree = BTree.createInstance( cacheRecordManager, ComparableComparator.INSTANCE, DefaultSerializer.INSTANCE, DefaultSerializer.INSTANCE, 1000 );
//            cacheRecordManager.setNamedObject( BTREE_NAME, btree.getRecid() );
//        }
        btree = BTree.createInstance( cacheRecordManager, ComparableComparator.INSTANCE, DefaultSerializer.INSTANCE, DefaultSerializer.INSTANCE, 1000 );
        cacheRecordManager.setNamedObject( BTREE_NAME, btree.getRecid() );
    }

    public void load(int treeid)throws IOException{
        String BTREE_NAME = String.valueOf(treeid);
        recid = cacheRecordManager.getNamedObject( BTREE_NAME );
        if ( recid != 0 ) {
            recid = cacheRecordManager.getNamedObject( BTREE_NAME );
            btree = BTree.load( cacheRecordManager, recid );
            //System.out.println("loading btree: " + btree.size());
        }
        else {
            System.out.println("Failed loading btree: " + treeid);
            System.exit(-1);
        }
    }

    public int getBtreeSize(){
        return btree.size();
    }
}