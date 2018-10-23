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

import java.util.HashMap;

/**
 * 公共初始化类，为库中所有对象提供公共构造函数和统一初始化，将字符串和对象一一关联，每个属性对应一个字符串
 * 功能：
 * 1、getProperty返回与给定字符串关联的Object
 * 2、setProperty将给定的Object与给定的字符串关联（保存Object的引用，不发生克隆）
 */
public class PropertySet
{
	private HashMap m_propertySet = new HashMap();

	public Object getProperty(String property)
	{
		return m_propertySet.get(property);
	}

	public void setProperty(String property, Object o)
	{
		m_propertySet.put(property, o);
	}
} // PropertySet
