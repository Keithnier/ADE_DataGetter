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

/**
 * 为了自定义查询，IVisitor接口（基于Visitor模式[gamma94]）为访问索引和叶节点以及数据条目提供回调函数。
 * 节点和数据信息可以使用INode和IData接口获得（都扩展IEntry）。
 * 使用这个接口的例子包括可视化查询，计算为特定查询访问的叶节点或索引节点的数量，在访问特定空间区域时抛出警报等。
 */
public interface IVisitor
{
	public void visitNode(final INode n);
	public void visitData(final IData d);
} // IVisitor
