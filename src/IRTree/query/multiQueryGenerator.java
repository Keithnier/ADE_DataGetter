package IRTree.query;

import IRTree.spatialindex.rtree.IRTree;
import IRTree.spatialindex.spatialindex.Point;
import IRTree.spatialindex.storagemanager.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


public class multiQueryGenerator {


	public static void main(String[] args)throws Exception{
		

		String tree_file = "1Rtree";
		String input_file = "Euro.good.gz";
		int numOfkeywords = 4;
		int topk = 301;
		

		int numOfQuery = 1000;
		//int numOfkeywords = 4;		


		int fanout = 100;
		int buffersize = 4096;
		double alpha =0.5;
		

		
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

		int haha = 1001;
		//////////////////////////////////
		//for(int haha = 1001; haha<=3001; haha+=2000){
		//////////////////////////////
		
		
		int[] numOfmissings = new int[] {100};
		
		System.out.println("c.size()    :  " + c.size());
		Integer[] keywords = new Integer[c.size()];
		
		c.toArray(keywords);
		String[] output_file = new String[4];
		
		for(i = 0; i< numOfmissings.length ; i++){
		 output_file[i] = "1000_euro_4word_"+numOfmissings[i]+"missing_before_"+haha+".txt";}
		
        PrintWriter[] bw = new PrintWriter[numOfmissings.length];
        
        for(i = 0; i<numOfmissings.length; i++){
        		bw[i] = new PrintWriter(output_file[i]);
        };
		
        
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
	    
	    ArrayList<Integer> missingobjects = irtree.Find_AllO_Rank_K(c, qpoint, haha, alpha);
	    
	    if(missingobjects == null) continue;
	    

	    Integer[] missings = new Integer[missingobjects.size()];
	    missingobjects.toArray(missings);
	    
	    for(i = 0; i<numOfmissings.length; i++){
	    	bw[i].print(numOfmissings[i]);
	    	bw[i].print(",");
	    	
	    	d.clear();
		    while(d.size() < numOfmissings[i]) {
		    	int j = (int) (Math.random()* missings.length );
		    	j = missings[j];
		    	
		    	while(d.contains(j)){
		    		j = (int) (Math.random()* missings.length );
		    	    j = missings[j];}
		    	
		    	bw[i].print(j);
		    	bw[i].print(",");
		    	d.add(j);
		    }

		    
	    	bw[i].print(f[0]);
	    	bw[i].print(",");
	    	bw[i].print(f[1]);
	    	
	    	iterator = c.iterator();
	    	
	    	while(iterator.hasNext()){
		    	bw[i].print(",");
                bw[i].print(iterator.next());  	    		
	    	}
	    	bw[i].println();

	    	
	      }
    	count++;
    	System.out.println(count);
	    for(i=0; i< numOfmissings.length; i++){
	          bw[i].flush();
	          }
	    }
	    

	    for(i=0; i<numOfmissings.length; i++)       bw[i].close();
	}
	///////
	//}
	///////
}		



