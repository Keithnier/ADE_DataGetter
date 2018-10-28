package IRTree.query;

import IRTree.spatialindex.rtree.IRTree;
import IRTree.spatialindex.spatialindex.Point;
import IRTree.spatialindex.storagemanager.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


public class singleQueryGenerator {


	public static void main(String[] args)throws Exception{
		
   // for(int round = 1800; round <=1800; round= round + 400){
		String tree_file = "1Rtree";
		String input_file = "euro.good.gz";

		int numOfkeywords = 4;
		//int topk = 31;
		

		int numOfQuery = 1000;



		int fanout = 100;
		int buffersize = 4096;
		double alpha = 0.5;
		

		
		PropertySet ps = new PropertySet();

		ps.setProperty("FileName", tree_file);


		Integer pagesize = new Integer(4096*fanout/100);
		ps.setProperty("PageSize", pagesize);
		ps.setProperty("BufferSize", buffersize);
		IStorageManager diskfile = new DiskStorageManager(ps);
		IBuffer file = new TreeLRUBuffer(diskfile, buffersize, false);
		ps.setProperty("IndexIdentifier", new Integer(1));
		IRTree irtree = new IRTree(ps, file, false);
	  
		String[] temp;  
		String[] temp2;
		int missingid = 0;
		int count = 0;
		int i = 0, id =0, cc = 0;
		int missingobject = 0;
		double f[] = new double[2];
		Iterator<Integer> iterator = null;
		Vector<Integer> c = new Vector<Integer> ();
		HashSet<Integer> d = new HashSet<Integer> ();
		
		FileInputStream fin = new FileInputStream(input_file);	
	    GZIPInputStream gzis = new GZIPInputStream(fin);
	    InputStreamReader xover = new InputStreamReader(gzis);
	    BufferedReader is = new BufferedReader(xover);
		
	    
	    c.clear();
	    String line;
		while ( (line=is.readLine()) != null){
			count++;
			temp = line.split(",");
			for(int j = 3; j < temp.length; j++){
				temp2 = temp[j].split(" ");
				id = Integer.parseInt(temp2[0]);
				if(!c.contains(id))
				c.add(id);
			}
	    }

		//int haha = 3001;
		//////////////////////////////////
		//for(int haha = 1001; haha<=3001; haha+=2000){
		//////////////////////////////
		
		
		//int[] numOfmissings = new int[] {1,3,10,30};
		
		System.out.println("c.size()    :  " + c.size());
		Integer[] keywords = new Integer[c.size()];
		
		c.toArray(keywords);
		
		
		
		String output_file = "1000_euro_4word_m501_a0.5_k010.txt";
		
        PrintWriter bw = new PrintWriter(output_file);
        
        
		count = 0;
	    while(count < numOfQuery){
	    	
		f[0] = Math.random();
		while(f[0]<=0.0 || f[0] >=1.0) f[0] = Math.random();
		f[1] = Math.random();
		while(f[1]<=0.0 || f[1] >=1.0) f[1] = Math.random();
		
	    Point qpoint = new Point(f);
	    
		c.clear();
	    while(c.size()< numOfkeywords){
	    	id = (int) (Math.random() * keywords.length);
	        id = keywords[id];
	        
	        while(c.contains(id)){
	        id = (int) (Math.random() * keywords.length);
		    id = keywords[id];	
	        }
	        c.add(id);
	    }
	    
	    int missingobjects = irtree.Find_O_Rank_K(c, qpoint, 501, alpha);
	    if(missingobjects==-1) continue;

	        	    


		        
	    	bw.print(missingobjects+","+f[0]+","+f[1]);
	    	//bw[i].print(",");
	    	//bw[i].print(f[1]);
	    	
	    	iterator = c.iterator();
	    	while(iterator.hasNext()){
		    	bw.print(",");
                bw.print(iterator.next());  	    		
	    	}
	    	bw.println();

	    	

    	count++;
    	System.out.println(count);
    	bw.flush();
	    }
	    bw.close();

	}
}		




