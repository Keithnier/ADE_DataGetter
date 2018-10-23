package spatialindex.storagemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public class TreeLRUBuffer extends Buffer {
	
	Hashtable pagecounter;

	public TreeLRUBuffer(IStorageManager sm, int capacity, boolean bWriteThrough)
	{
		super(sm, capacity, bWriteThrough);
		pagecounter = new Hashtable();
		
	}
	void addEntry(int id, Entry e)
	{
		assert m_buffer.size() <= m_capacity;

		if (m_buffer.size() == m_capacity) removeEntry();
		m_buffer.put(new Integer(id), e);
		if(pagecounter.containsKey(id)){
			PageCounter pc = (PageCounter)pagecounter.get(id);
			pc.increaseCounter();
		}
		else{
			PageCounter pc = new PageCounter(id);
			pagecounter.put(id, pc);
		}
	}

	void removeEntry()
	{
		if (m_buffer.size() == 0) return;
		
		ArrayList list = new ArrayList(pagecounter.values());
		
		Collections.sort(list, new PageCounterComparatorAsc());
		
		PageCounter rm = (PageCounter)list.get(0);

		int removedID = rm.getID();
		
		pagecounter.remove(removedID);

		Entry e = (Entry) m_buffer.get(removedID);
		

		if (e.m_bDirty)
		{
			m_storageManager.storeByteArray(removedID, e.m_data);
		}

		m_buffer.remove(new Integer(removedID));
	}

}
