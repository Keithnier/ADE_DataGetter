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

package IRTree.spatialindex.spatialindex;

import IRTree.spatialindex.storagemanager.PropertySet;

/**
 * 空间索引是任何有效访问空间信息的索引结构。它可以从简单的网格文件到复杂的树结构。
 * 空间索引对IEntry类型的条目进行索引，这些条目可以是索引节点，叶节点，数据等，具体取决于结构特征。
 * 应为所有类型的条目提供适当的访问器方法接口。
 *
 * 当创建新的索引时，应该为其分配一个唯一的索引ID，这将在从持久存储重新加载索引时使用。
 * 此索引ID应作为用于构建索引实例的PropertySet实例中的IndexIdentifier属性返回。
 * 使用索引ID，多个索引可以存储在同一个存储管理器中。 管理索引ID是用户的责任。
 * 将错误的索引ID与错误的存储管理器或索引类型关联会导致未定义的结果。
 */
public interface ISpatialIndex
{
	public void flush() throws IllegalStateException;

	/**
	 * 可以使用insertData方法插入数据条目。插入函数将根据索引将任何形状转换为内部表示形式。应为每个插入的对象分配一个ID（称为对象标识符），以便更新，删除和报告对象。
	 * 调用者有责任为索引提供ID（唯一或不唯一）。而且，一个字节数组可以与一个条目相关联。字节数组与叶节点内的空间信息一起存储。可以通过这种方式支持聚集索引。字节数组也可以为null，并且每个节点不应该使用额外的空间。
	 */
	public void insertData(final byte[] data, final IShape shape, int id);

	/**
	 * 可以使用deleteData方法删除数据条目。应提供对象形状和ID。空间索引根据空间特征而不是ID来聚类对象。因此，该形状对于查找和删除条目至关重要。
	 */
	public boolean deleteData(final IShape shape, int id);

	/**
	 * containmentQuery方法需要一个查询形状和对有效IVisitor实例的引用
	 * 如果查询形状是简单的区域，则会执行经典范围查询。用户有能力创建自己的形状，因此定义了自己的交集和包含方法，使得可以运行任何类型的范围查询而无需修改索引。
	 * 例如，如果使用rtree索引，则由于所有rtree节点都是Region类型，因此梯形应该定义它自身与区域之间的相交和包含。因此，用户应该对索引内部表示有一些了解，以运行更复杂的查询。
	 */
	public void containmentQuery(final IShape query, final IVisitor v);
	public void intersectionQuery(final IShape query, final IVisitor v);

	/**
	 * 点位置查询是使用pointLocationQuery方法执行的。它将查询点和访问者作为参数。
	 */
	public void pointLocationQuery(final IShape query, final IVisitor v);

	/**
	 * 使用nearestNeighborQuery方法可以执行最近邻居查询。它的第一个参数是要求的最近邻居的数量k。此方法还需要查询形状和访问者对象。
	 * 默认实现使用IShape的getMinimumDistance函数来计算查询与矩形节点和存储在树中的数据项之间的距离。
	 * 通过实现INearestNeighborComparator接口并将它作为nearestNeighborQuery的最后一个参数传递，可以使用更复杂的距离度量。
	 */
	public void nearestNeighborQuery(int k, final IShape query, final IVisitor v, INearestNeighborComparator nnc);
	public void nearestNeighborQuery(int k, final IShape query, final IVisitor v);

	/**
	 * queryStrategy方法提供了设计更复杂查询的能力。
	 * 它使用IQueryStrategy接口作为不断请求的call-back，直到不再需要更多条目。它可以用来实现自定义查询算法（基于策略模式[gamma94]）。
	 */
	public void queryStrategy(final IQueryStrategy qs);

	/**
	 * 方法getIndexProperties返回一个PropertySet，其中包含所有有用的索引属性，如维度等。
	 */
	public PropertySet getIndexProperties();

	/**
	 * NodeCommand接口用于定制节点操作。使用addWriteNodeCommand，addReadNodeCommand和addDeleteNodeCommand方法，将自定义命令对象添加到侦听器列表中，并在相应的操作之后执行。
	 */
	public void addWriteNodeCommand(INodeCommand nc);
	public void addReadNodeCommand(INodeCommand nc);
	public void addDeleteNodeCommand(INodeCommand nc);

	/**
	 *isIndexValid方法执行内部检查以测试结构的完整性。它用于调试目的。
	 */
	public boolean isIndexValid();

	/**
	 * 通过IStatistics接口和getStatistics方法提供有用的统计信息。
	 */
	public IStatistics getStatistics();
} // ISpatialIndex

