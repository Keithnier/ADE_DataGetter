package IRTree.regressiontest;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

class NNEntry
{
	public int m_id;
	public double m_dist;

	NNEntry(int id, double dist) { m_id = id; m_dist = dist; }
}

/**
 * 该类实现对data数据的读取（读入内存），包括data和query，并实现
 * 1、判断query和data是否相交，若相交则输出
 * 2、计算query和data的10NN个对象并输出（结果存在并列情况，可能不止10个）
 * data格式：op(0为删除1为插入2为查询) id x1 y1 x2 y2
 */
public class Exhaustive
{
	public static void main(String[] args)
	{
		System.out.println("请输入data_file和query_type[intersection | 10NN]:");
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		String [] inputList = input.split(" ");
		if (inputList.length != 2)
		{
			System.err.println("Usage: Exhaustive data_file query_type [intersection | 10NN].");
			System.exit(-1);
		}

		String data_file = inputList[0];
		String query_type = inputList[1];

		LineNumberReader lr = null;

		try
		{
			lr = new LineNumberReader(new FileReader(data_file));
		}
		catch (FileNotFoundException e)
		{
			System.err.println("Cannot open data file" + data_file + ".");
			System.exit(-1);
		}

		HashMap data = new HashMap();
		int id, op;
		float x1, x2, y1, y2;
		String line = null;

		try
		{
			line = lr.readLine();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		while (line != null)
		{
			StringTokenizer st = new StringTokenizer(line);
			op = new Integer(st.nextToken()).intValue();
			id = new Integer(st.nextToken()).intValue();

			if (op == 0)
			{
				//delete
			}
			else if (op == 1)
			{
				//insert
				x1 = new Float(st.nextToken()).floatValue();
				y1 = new Float(st.nextToken()).floatValue();
				x2 = new Float(st.nextToken()).floatValue();
				y2 = new Float(st.nextToken()).floatValue();

				data.put(new Integer(id), new MyRegion(x1, y1, x2, y2));
			}
			else if (op == 2)
			{
				//query
				x1 = new Float(st.nextToken()).floatValue();
				y1 = new Float(st.nextToken()).floatValue();
				x2 = new Float(st.nextToken()).floatValue();
				y2 = new Float(st.nextToken()).floatValue();

				if (query_type.equals("intersection"))
				{
					MyRegion query = new MyRegion(x1, y1, x2, y2);
					Iterator it = data.entrySet().iterator();
					while (it.hasNext())
					{
						Map.Entry e = (Map.Entry) it.next();
						MyRegion r = (MyRegion) e.getValue();
						Integer i = (Integer) e.getKey();
						if (query.intersects(r))
						{
							System.out.println(i.intValue());
						}
					}
				}
				else if (query_type.equals("10NN"))
				{
					MyRegion query = new MyRegion(x1, y1, x2, y2);

					// there is no priority queue implementation in Java. Even TreeSet or TreeMap require unique keys,
					// and since I am sorting according to minDist, I need to be able to keep duplicate values.
					// The problem with Arrays.sort is nlogn time, when the priority queue would be logn.
					NNEntry[] queue = new NNEntry[data.size()];

					int cIndex = 0;
					Iterator it = data.entrySet().iterator();
					while (it.hasNext())
					{
						Map.Entry e = (Map.Entry) it.next();
						MyRegion r = (MyRegion) e.getValue();
						Integer i = (Integer) e.getKey();
						System.out.println(r.getMinDist(query));
						queue[cIndex++] = new NNEntry(i.intValue(), r.getMinDist(query));
					}

					Arrays.sort(queue,
											new Comparator()
											{
												public int compare(Object o1, Object o2)
												{
													NNEntry n1 = (NNEntry) o1;
													NNEntry n2 = (NNEntry) o2;

													if (n1.m_dist < n2.m_dist) return -1;
													if (n1.m_dist > n2.m_dist) return 1;
													return 0;
												}
											});

					int count = 0;
					double knearest = 0.0;

					for (cIndex = 0; cIndex < queue.length; cIndex++)
					{
						if (count >= 10 && queue[cIndex].m_dist > knearest) break;

						System.out.println(queue[cIndex].m_id);
						count++;
						knearest = queue[cIndex].m_dist;
					}
				}
			}

			try
			{
				line = lr.readLine();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		} // while
	} // main
}
