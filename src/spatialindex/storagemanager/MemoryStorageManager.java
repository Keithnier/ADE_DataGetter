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

package spatialindex.storagemanager;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 由于它暗示着名称，这是一个主要的内存实现。一切都使用简单的向量存储在主存中。
 * 没有属性需要初始化MemoryStorageManager对象。当MemoryStorageManager实例超出作用域时，它所包含的所有数据都将丢失。
 */
public class MemoryStorageManager implements IStorageManager
{
	private ArrayList m_buffer = new ArrayList();
	private Stack m_emptyPages = new Stack();

	public void flush()
	{
	}

	public byte[] loadByteArray(final int id)
	{
		Entry e = null;

		try
		{
			e = (Entry) m_buffer.get(id);
		}
		catch (IndexOutOfBoundsException ex)
		{
			throw new InvalidPageException(id);
		}

		byte[] ret = new byte[e.m_pData.length];
		System.arraycopy(e.m_pData, 0, ret, 0, e.m_pData.length);
		return ret;
	}

	public int storeByteArray(final int id, final byte[] data)
	{
		int ret = id;
		Entry e = new Entry(data);

		if (id == NewPage)
		{
			if (m_emptyPages.empty())
			{
				m_buffer.add(e);
				ret = m_buffer.size() - 1;
			}
			else
			{
				ret = ((Integer) m_emptyPages.pop()).intValue();
				m_buffer.set(ret, e);
			}
		}
		else
		{
			if (id < 0 || id >= m_buffer.size()) throw new InvalidPageException(id);
			m_buffer.set(id, e);
		}

		return ret;
	}

	public void deleteByteArray(final int id)
	{
		Entry e = null;
		try
		{
			e = (Entry) m_buffer.get(id);
		}
		catch (IndexOutOfBoundsException ex)
		{
			throw new InvalidPageException(id);
		}

		m_buffer.set(id, null);
		m_emptyPages.push(new Integer(id));
	}

	class Entry
	{
		byte[] m_pData;

		Entry(final byte[] d)
		{
			m_pData = new byte[d.length];
			System.arraycopy(d, 0, m_pData, 0, d.length);
		}
	} // Entry
}
