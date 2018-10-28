package IRTree.neustore.base;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * An float data. 
 * @author Dingming Wu
 */
public class FloatData implements Data, Serializable {
	public float data;
	public float data2 = 0;
	
	public FloatData( float _data ) {
		data = _data;
	}
	
	public FloatData(float _data, float _data2){
		data  = _data;
		data2 = _data2;
	}
	
	public Object clone() {
		FloatData newData = new FloatData(data);
		return newData;
	}
	
	public int size() { return 8; }
	
	public int maxSize() { return 8;}
	
	public void read(DataInputStream in) throws IOException {
		data = in.readFloat();
		data2 = in.readFloat();
	}
	public void write(DataOutputStream out) throws IOException {
		out.writeFloat(data);
		out.writeFloat(data2);
	}

	public int compareTo(Data data2) {
		Float i1 = new Float(data);
		Float i2 = new Float( ((FloatData)data2).data );
		return i1.compareTo(i2);
	}
	
	public boolean equals(Object data2) {
		float d = ((FloatData)data2).data;
		return data==d;
	}
}

