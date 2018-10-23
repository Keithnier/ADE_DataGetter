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

package spatialindex.spatialindex;

public class Point implements IShape, Cloneable
{
	public double[] m_pCoords = null;

	public Point(double[] pCoords)
	{
		m_pCoords = new double[pCoords.length];
		System.arraycopy(pCoords, 0, m_pCoords, 0, pCoords.length);
	}

	public Point(final Point pt)
	{
		m_pCoords = new double[pt.m_pCoords.length];
		System.arraycopy(pt.m_pCoords, 0, m_pCoords, 0, pt.m_pCoords.length);
	}

	public boolean equals(Object o)
	{
		if (o instanceof Point)
		{
			Point pt = (Point) o;

			if (pt.m_pCoords.length != m_pCoords.length) return false;

			for (int cIndex = 0; cIndex < m_pCoords.length; cIndex++)
			{
				// double数值之间无法直接比较，这里采用偏移值的方法。如果左右差距大于一个EPSILON，则认为结点不相等
				if (m_pCoords[cIndex] < pt.m_pCoords[cIndex] - SpatialIndex.EPSILON ||
						m_pCoords[cIndex] > pt.m_pCoords[cIndex] + SpatialIndex.EPSILON) return false;
			}

			return true;
		}

		return false;
	}

	//
	// Cloneable interface
	//

	public Object clone()
	{
		return new Point(m_pCoords);
	}

	//
	// IShape interface
	//

	public boolean intersects(final IShape s)
	{
		// 结点的相交判断，通过其中一个区域是否包含其坐标来判断
		if (s instanceof Region) return ((Region) s).contains(this);

		return false;
	}

	// Point映射为坐标点，或者查询点，点与点之间不存在包含关系
	public boolean contains(final IShape s)
	{
		return false;
	}

	public boolean touches(final IShape s)
	{
		if (s instanceof Point && this.equals(s)) return true;

		if (s instanceof Region) return ((Region) s).touches(this);

		return false;
	}

	// 点的中心就是其坐标点。
	public double[] getCenter()
	{
		double[] pCoords = new double[m_pCoords.length];
		System.arraycopy(m_pCoords, 0, pCoords, 0, m_pCoords.length);
		return pCoords;
	}

	public long getDimension()
	{
		return m_pCoords.length;
	}

	public Region getMBR()
	{
		return new Region(m_pCoords, m_pCoords);
	}

	// 点没有面积
	public double getArea()
	{
		return 0.0f;
	}

	// 查询操作中，返回距离
	public double getMinimumDistance(final IShape s)
	{
		if (s instanceof Region) return ((Region) s).getMinimumDistance(this);

		if (s instanceof Point) return getMinimumDistance((Point) s);

		throw new IllegalStateException("getMinimumDistance: Not implemented yet!");
	}

	// 各个维度上点到点之间的距离，勾股定理及其拓展
	double getMinimumDistance(final Point p)
	{
		if (m_pCoords.length != p.m_pCoords.length) throw new IllegalArgumentException("getMinimumDistance: Shape has the wrong number of dimensions.");

		double ret = 0.0;

		for (int cIndex = 0; cIndex < m_pCoords.length; cIndex++)
		{
			ret += Math.pow(m_pCoords[cIndex] - p.m_pCoords[cIndex], 2.0);
		}

		return Math.sqrt(ret);
	}

	// 获得点在指定维度上的坐标
	public double getCoord(int index) throws IndexOutOfBoundsException
	{
		if (index >= m_pCoords.length) throw new IndexOutOfBoundsException("" + index);
		return m_pCoords[index];
	}
}
