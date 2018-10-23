package neustore.base;

import java.io.Serializable;

/**
 * A class that stores a pair of Key and Data.
 * 键值对
 * @author Donghui Zhang &lt;donghui@ccs.neu.edu&gt;
 */
public class KeyData implements Serializable{
	public Key key;
	public Data data;
	
	public KeyData(Key key, Data data) {
		this.key = key;
		this.data = data;
	}
}