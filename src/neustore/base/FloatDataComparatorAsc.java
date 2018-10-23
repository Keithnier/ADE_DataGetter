package neustore.base;

import java.util.Comparator;

public class FloatDataComparatorAsc implements Comparator<KeyData> {
	
	public int compare(KeyData r1, KeyData r2){
		FloatData d1 = (FloatData)r1.data;
		FloatData d2 = (FloatData)r2.data;
		IntKey id1 = (IntKey)r1.key;
		IntKey id2 = (IntKey)r2.key;
		if(d1.data < d2.data) return -1;
		else if(d1.data > d2.data) return 1;
		else if(id1.key < id2.key) return -1;
		else if(id1.key > id2.key) return 1;
		else return 0;
	}
}
