// Spatial Index Library
//
// Copyright (C) 2002  Navel Ltd.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
// Contact information:
//  Mailing address:
//    Marios Hadjieleftheriou
//    University of California, Riverside
//    Department of Computer Science
//    Surge Building, Room 310
//    Riverside, CA 92521
//
//  Email:
//    marioh@cs.ucr.edu

package IRTree.spatialindex.storagemanager;

import util.Constants;

import java.io.*;
import java.util.*;

/**
 * 磁盘存储管理器使用两个随机访问文件来存储信息。
 * 一个扩展名为.idx，另一个扩展名为.dat
 * .idx文件的目的是存储重要信息，如页面大小，下一个可用页面，空白页面列表以及与每个实体ID关联的页面序列。
 * 该类还提供了一种实际上覆盖.idx文件并同步两个文件指针的刷新方法。
 * .idx文件在初始化过程中被加载到主内存中，并且仅在刷新存储管理器或对象完成期间写入磁盘。
 * 如果发生意外故障，存储管理器的更改将由于.idx文件过时而丢失。避免这种灾难是未来的工作。
 * 重要提示：请记住在处理索引之前调用ISpatialIndex.flush（），因为在JVM退出时不会发生一些终止事件。
 */

/**
 * 这里给出.idx文件的结构
 * +-----------+----------+------------+-------------+-----------+--------+-------------+-----------------+--------+
 * | pageSize | nextPage | emptyCount | emptyNum... | entryCount| entryID| entryLength | pageCountOfEntry| pageID |
 * +---------+----------+------------+-------------+-----------+--------+-------------+------------------+-------+
 * 页面大小，下一页id，空页数目，空页id...，实体数目，实体id，实体长度，实体中页面数目，实体中页面id....
 */
public class DiskStorageManager implements IStorageManager {
    private RandomAccessFile m_dataFile = null;
    private RandomAccessFile m_indexFile = null;
    private int m_pageSize = 0;
    private int m_nextPage = -1;
    private TreeSet m_emptyPages = new TreeSet();
    private HashMap m_pageIndex = new HashMap();
    private byte[] m_buffer = null;


    /**
     * FileName	String
     * 要打开的文件的基本名称（无扩展名）
     *    * Overwrite	Boolean
     *    * 如果覆盖为真，并且具有指定文件名的存储管理器已存在，则它将被截断并被覆盖。所有数据都将丢失
     *  	* PageSize	Integer
     * 要使用的页面大小。如果指定的文件名已经存在并且Overwrite为false，则忽略PageSize
     */
    public DiskStorageManager(PropertySet ps)
            throws SecurityException, NullPointerException, IOException, FileNotFoundException, IllegalArgumentException {
        Object var;

        // Open/Create flag.
        boolean bOverwrite = false;
        var = ps.getProperty("Overwrite");

        if (var != null) {
            if (!(var instanceof Boolean)) throw new IllegalArgumentException("Property Overwrite must be a Boolean");
            bOverwrite = ((Boolean) var).booleanValue();
        }

        // storage filename.
        var = ps.getProperty("FileName");

        if (var != null) {
            if (!(var instanceof String)) throw new IllegalArgumentException("Property FileName must be a String");

            File indexFile = new File(Constants.SAVEDATA_DIRECTORY +  File.separator + (String) var + ".idx");
            File dataFile = new File(Constants.SAVEDATA_DIRECTORY +  File.separator + (String) var + ".dat");

            // check if files exist.
            // 如果文件不存在，将创建文件。
            if (bOverwrite == false && (!indexFile.exists() || !dataFile.exists())) bOverwrite = true;

            if (bOverwrite) {
                if (indexFile.exists()) indexFile.delete();
                if (dataFile.exists()) dataFile.delete();

                boolean b = indexFile.createNewFile();
                if (b == false) throw new IOException("Index file cannot be opened.");

                b = dataFile.createNewFile();
                if (b == false) throw new IOException("Data file cannot be opened.");
            }

            m_indexFile = new RandomAccessFile(indexFile, "rw");
            m_dataFile = new RandomAccessFile(dataFile, "rw");
        } else {
            throw new IllegalArgumentException("Property FileName was not specified.");
        }

        // find page size.
        // 配置页面大小，以及下一页的序号。
        if (bOverwrite == true) {
            // Overwrite == true 意味着.idx文件是新文件，必须在配置中声明页面大小，且初始化页号一定为0
            var = ps.getProperty("PageSize");

            if (var != null) {
                if (!(var instanceof Integer))
                    throw new IllegalArgumentException("Property PageSize must be an Integer");
                m_pageSize = ((Integer) var).intValue();
                m_nextPage = 0;
            } else {
                throw new IllegalArgumentException("Property PageSize was not specified.");
            }
        } else {
            try {
                m_pageSize = m_indexFile.readInt();
            } catch (EOFException ex) {
                throw new IllegalStateException("Failed reading pageSize.");
            }

            try {
                m_nextPage = m_indexFile.readInt();
            } catch (EOFException ex) {
                throw new IllegalStateException("Failed reading nextPage.");
            }
        }

        // create buffer.
        // 从磁盘文件读入数据到内存
        m_buffer = new byte[m_pageSize];

        if (bOverwrite == false) {
            int count, id, page;

            // load empty pages in memory.
            try {
                count = m_indexFile.readInt();

                for (int cCount = 0; cCount < count; cCount++) {
                    page = m_indexFile.readInt();
                    m_emptyPages.add(new Integer(page));
                }

                // load index table in memory.
                count = m_indexFile.readInt();

                for (int cCount = 0; cCount < count; cCount++) {
                    Entry e = new Entry();

                    id = m_indexFile.readInt();
                    e.m_length = m_indexFile.readInt();

                    int count2 = m_indexFile.readInt();

                    for (int cCount2 = 0; cCount2 < count2; cCount2++) {
                        page = m_indexFile.readInt();
                        e.m_pages.add(new Integer(page));
                    }
                    m_pageIndex.put(new Integer(id), e);
                }
            } catch (EOFException ex) {
                throw new IllegalStateException("Corrupted index file.");
            }
        }
    }

    public void flush() {
        try {
            // 这个字节是-1吗？
            m_indexFile.seek(0l);

            m_indexFile.writeInt(m_pageSize);
            m_indexFile.writeInt(m_nextPage);

            int id, page;
            int count = m_emptyPages.size();

            m_indexFile.writeInt(count);

            Iterator it = m_emptyPages.iterator();
            while (it.hasNext()) {
                page = ((Integer) it.next()).intValue();
                m_indexFile.writeInt(page);
            }

            count = m_pageIndex.size();
            m_indexFile.writeInt(count);

            it = m_pageIndex.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry me = (Map.Entry) it.next();
                id = ((Integer) me.getKey()).intValue();
                m_indexFile.writeInt(id);

                Entry e = (Entry) me.getValue();
                count = e.m_length;
                m_indexFile.writeInt(count);

                count = e.m_pages.size();
                m_indexFile.writeInt(count);

                for (int cIndex = 0; cIndex < count; cIndex++) {
                    page = ((Integer) e.m_pages.get(cIndex)).intValue();
                    m_indexFile.writeInt(page);
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Corrupted index file.");
        }
    }

    public byte[] loadByteArray(final int id) {
        Entry e = (Entry) m_pageIndex.get(new Integer(id));
        if (e == null) throw new InvalidPageException(id);

        int cNext = 0;
        int cTotal = e.m_pages.size();

        byte[] data = new byte[e.m_length];
        int cIndex = 0;
        int cLen;
        int cRem = e.m_length;

        do {
            try {
                m_dataFile.seek(((Integer) e.m_pages.get(cNext)).intValue() * m_pageSize);
                int bytesread = m_dataFile.read(m_buffer);
                if (bytesread != m_pageSize) throw new IllegalStateException("Corrupted data file.");
            } catch (IOException ex) {
                throw new IllegalStateException("Corrupted data file.");
            }

            cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
            System.arraycopy(m_buffer, 0, data, cIndex, cLen);

            cIndex += cLen;
            cRem -= cLen;
            cNext++;
        }
        while (cNext < cTotal);

        return data;
    }

    public int storeByteArray(final int id, final byte[] data) {
        if (id == NewPage) {
            Entry e = new Entry();
            e.m_length = data.length;

            int cIndex = 0;
            int cPage;
            int cRem = data.length;
            int cLen;

            while (cRem > 0) {
                if (!m_emptyPages.isEmpty()) {
                    Integer i = (Integer) m_emptyPages.first();
                    m_emptyPages.remove(i);
                    cPage = i.intValue();
                } else {
                    cPage = m_nextPage;
                    m_nextPage++;
                }

                cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
                System.arraycopy(data, cIndex, m_buffer, 0, cLen);

                try {
                    m_dataFile.seek(cPage * m_pageSize);
                    m_dataFile.write(m_buffer);
                } catch (IOException ex) {
                    throw new IllegalStateException("Corrupted data file.");
                }

                cIndex += cLen;
                cRem -= cLen;
                e.m_pages.add(new Integer(cPage));
            }

            Integer i = (Integer) e.m_pages.get(0);
            m_pageIndex.put(i, e);

            return i.intValue();
        } else {
            // find the entry.
            Entry oldEntry = (Entry) m_pageIndex.get(new Integer(id));
            if (oldEntry == null) throw new InvalidPageException(id);

            m_pageIndex.remove(new Integer(id));

            Entry e = new Entry();
            e.m_length = data.length;

            int cIndex = 0;
            int cPage;
            int cRem = data.length;
            int cLen, cNext = 0;

            while (cRem > 0) {
                if (cNext < oldEntry.m_pages.size()) {
                    cPage = ((Integer) oldEntry.m_pages.get(cNext)).intValue();
                    cNext++;
                } else if (!m_emptyPages.isEmpty()) {
                    Integer i = (Integer) m_emptyPages.first();
                    m_emptyPages.remove(i);
                    cPage = i.intValue();
                } else {
                    cPage = m_nextPage;
                    m_nextPage++;
                }

                cLen = (cRem > m_pageSize) ? m_pageSize : cRem;
                System.arraycopy(data, cIndex, m_buffer, 0, cLen);

                try {
                    m_dataFile.seek(cPage * m_pageSize);
                    m_dataFile.write(m_buffer);
                } catch (IOException ex) {
                    throw new IllegalStateException("Corrupted data file.");
                }

                cIndex += cLen;
                cRem -= cLen;
                e.m_pages.add(new Integer(cPage));
            }

            while (cNext < oldEntry.m_pages.size()) {
                m_emptyPages.add(oldEntry.m_pages.get(cNext));
                cNext++;
            }

            Integer i = (Integer) e.m_pages.get(0);
            m_pageIndex.put(i, e);

            return i.intValue();
        }
    }

    public void deleteByteArray(final int id) {
        // find the entry.
        Entry e = (Entry) m_pageIndex.get(new Integer(id));
        if (e == null) throw new InvalidPageException(id);

        m_pageIndex.remove(new Integer(id));

        for (int cIndex = 0; cIndex < e.m_pages.size(); cIndex++) {
            m_emptyPages.add(e.m_pages.get(cIndex));
        }
    }

    public void close() {
        flush();
    }

    class Entry {
        int m_length = 0;
        ArrayList m_pages = new ArrayList();
    }
}
