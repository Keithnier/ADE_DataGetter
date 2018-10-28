package IRTree.spatialindex.storagemanager;

import java.util.Comparator;

public class PageCounterComparatorAsc implements Comparator{

	public int compare(Object o1, Object o2){
		PageCounter pc1 = (PageCounter)o1;
		PageCounter pc2 = (PageCounter)o2;
		if(pc1.getCounter() < pc2.getCounter()) return -1;
		else if(pc1.getCounter() > pc2.getCounter()) return 1;
		else return 0;
	}
}
