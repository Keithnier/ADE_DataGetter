package spatialindex.rtree;

import spatialindex.spatialindex.IData;
import spatialindex.spatialindex.IShape;
import spatialindex.spatialindex.Region;

import java.io.Serializable;

public class Data implements IData, Serializable {
    int m_id;
    Region m_shape;
    byte[] m_pData;


    Data(byte[] pData, Region mbr, int id) {
        m_id = id;
        m_shape = mbr;
        m_pData = pData;
    }

    public Data(Region mbr, int id) {
        m_id = id;
        m_shape = mbr;
    }

    public int getIdentifier() {
        return m_id;
    }

    public IShape getShape() {
        return m_shape;
    }

    public byte[] getData() {
        // 本项目主旨在查询，即数据不可改动，这里返回数据的副本，而不能暴露其引用
        byte[] data = new byte[m_pData.length];
        System.arraycopy(m_pData, 0, data, 0, m_pData.length);
        return data;
    }

}
