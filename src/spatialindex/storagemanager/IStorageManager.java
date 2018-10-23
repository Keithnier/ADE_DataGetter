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

/**
 * 为所有索引的存储管理提供了一个通用界面，提供了存储和检索实体的功能。一个实体被视为一个简单的字节数组。
 * 存储管理器界面是通用的，不仅适用于空间索引。
 * 实现IStorageManager接口的类决定如何存储实体。提供了一个简单的主存储器实现，
 * 例如，使用向量存储实体，将每个实体与唯一ID（向量中的条目索引）相关联。
 * 基于磁盘的存储管理器可以选择将实体存储在简单的随机访问文件中，或者数据库存储管理器可以将它们存储在关系表等中，只要唯一ID与每个实体关联即可。
 * 此外，存储管理员应该从调用者（无论是索引还是用户）透明地实施他们自己的分页，压缩和删除策略。
 *
 * 存储管理员应该没有关于存储的实体类型的信息。
 * 这个决定有三个主要原因：
  1.任意数量的空间索引可以使用任意数量的页面和唯一的页面存储在单个存储管理器中（即，相同的关系表，或二进制文件或散列表等），以存储多个索引每个索引的索引ID（这将在短期内讨论）。
  2.可以支持聚类和非聚类索引。聚集索引存储与其包含的条目相关的数据及其索引的空间信息。非聚集索引仅存储其条目的空间信息。任何关联的数据都会分开存储，并通过唯一ID与索引条目关联。为了支持这两种类型的索引，存储管理器接口应该是非常通用的，允许索引决定如何存储其数据。否则，聚集索引和非聚集索引将不得不单独实施。
  3.决定灵活性。例如，用户可以选择一个聚集索引来处理所有内容。他们可以选择主内存非聚簇索引并将实际数据存储在MySQL中。他们可以选择基于磁盘的非聚簇索引，并将数据手动存储在单独的二进制文件中，或者甚至存储在同一个存储管理器中，但是执行低级别定制数据处理。

 * 目前的实施中提供了两个存储管理器：
  1.MemoryStorageManager
  2.DiskStorageManager
 */
public interface IStorageManager
{
	public static final int NewPage = -1;

	public void flush();

	/**
	 * loadByteArray方法获取实体标识并返回关联的字节数组。如果请求了无效的ID，则会引发异常。
	 */
	public byte[] loadByteArray(final int id);

	/**
	 * storeByteArray方法获取一个字节数组和一个实体ID。
	 * 如果调用者将NewPage指定为输入ID，则存储管理器将分配一个新ID，存储该实体并返回与该实体关联的ID。
	 * 相反，如果用户指定已存在的ID，则存储管理器将覆盖旧数据。 如果调用者请求覆盖无效ID，则会引发异常。
	 */
	public int storeByteArray(final int id, final byte[] data);

	/**
	 * deleteByteArray方法从存储中删除请求的实体。
	 */
	public void deleteByteArray(final int id);
} // IStorageManager
