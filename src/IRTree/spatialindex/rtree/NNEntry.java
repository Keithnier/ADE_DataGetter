package IRTree.spatialindex.rtree;

import IRTree.spatialindex.spatialindex.IEntry;

import java.io.Serializable;

public class NNEntry implements Serializable {

    public IEntry m_pEntry;
    public double m_minDist;
    public double normalized_dist;
    public double normalized_simi;
    public double mindist;
    public double minsimi;
    public double x1 = 0;
    public double x2 = 0;
    public int num = 0;
    public int level;
    public int kind = 0;
    public int before = 0;
    public int after = 0;


    public NNEntry(IEntry e, double f) {
        m_pEntry = e;
        m_minDist = f;
    }

    public NNEntry(IEntry e, double f, int l, double x, double y) {
        m_pEntry = e;
        m_minDist = f;
        level = l;
        normalized_dist = x;
        normalized_simi = y;
    }

    public NNEntry(IEntry e, double f, int l, int bb, int aa) {
        m_pEntry = e;
        level = l;
        m_minDist = f;
        before = bb;
        after = aa;
    }


    public NNEntry(IEntry m_pEntry, double m_minDist, int level) {
        this.m_pEntry = m_pEntry;
        this.m_minDist = m_minDist;
        this.level = level;
    }


    public boolean pruned;
}
