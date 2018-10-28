package IRTree.neustore.base;

import java.io.*;

/**
 * A class that provides basic read/write operators on a byte array.
 *
 * @author Donghui Zhang &lt;donghui@ccs.neu.edu&gt;
 */
public class ByteArray {

	public static final boolean READ = true;
	public static final boolean WRITE = false;

	private ByteArrayInputStream byte_in = null;
	private DataInputStream in = null;
	private byte[] buf = null; //一块内存，即一块存储空间
	private int offset = 0; // 内存空间中的字节偏移量

	/**
	 * Constructor.
	 * @param _buf     the associated byte array
	 * @param isRead   whether to read or write
	 */
	public ByteArray( byte[] _buf, boolean isRead ) {
		//如果可读，那么就产生一个输入流
		if ( isRead ) {
			byte_in = new ByteArrayInputStream(_buf);
			in = new DataInputStream(byte_in);
		}
		else {
			buf = _buf;
		}
	}

	/**
	 * Reads an integer.
	 * @return the integer
	 * @throws IOException
	 */
	public int readInt() throws IOException {
		return in.readInt();
	}

	/**
	 * Reads from the byte array to fill a passed buffer.
	 * Note: the number of bytes read is typically equal to <code>b.length</code>.
	 * @param b   buffer to read to.
	 * @throws IOException
	 */
	public void read( byte[] b ) throws IOException {
		in.read(b);
	}

	/**
	 * Writes an integer.
	 * @param value   the integer to write
	 * @throws IOException
	 */
	public void writeInt( int value ) throws IOException {
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream(4);
		DataOutputStream out = new DataOutputStream(byte_out);
		out.writeInt( value );
		System.arraycopy(byte_out.toByteArray(), 0, buf, offset, 4 );
		offset += 4;
		out.close();
		byte_out.close();
	}

	public void writeLong( long value ) throws IOException {
		ByteArrayOutputStream byte_out = new ByteArrayOutputStream(8);
		DataOutputStream out = new DataOutputStream(byte_out);
		out.writeLong( value );
		System.arraycopy(byte_out.toByteArray(), 0, buf, offset, 8 );
		offset += 8;
		out.close();
		byte_out.close();
	}
	/**
	 * Writes the passed buffer to the byte array.
	 * @param b  the passed buffer
	 * @throws IOException
	 */
	public void write( byte[] b ) throws IOException {
		System.arraycopy( b, 0, buf, offset, b.length);
	}
}