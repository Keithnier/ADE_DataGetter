package query;

import java.io.*;
import java.util.zip.GZIPInputStream;


public class DatasizeGenerator {

/*
	public static void main(String[] args)throws Exception{
	
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream("Euro.good.gz"));
	    InputStreamReader xover = new InputStreamReader(gzis);
	    BufferedReader is = new BufferedReader(xover);
	    
	    HashSet set = new HashSet();
	    String[] lines = new String[2000000]; 
	    int num = 0;
	    int c = 0;
	    int i = 0;
	    	    
	    
	    String line;
		while ( (line=is.readLine()) != null) {
			lines[num]  = line;
			num++;
			
			if(num<=100) System.out.println(line);
			
		}

		System.exit(1);
		System.out.println(num);
	    String outfile = "GN_";
		
		//for(int total = 1000000; total<=1000000; total=total+400000){
	    int total = 1400000;
			System.out.println(total);
			
			
		    FileOutputStream fou = new FileOutputStream("reorder_GN"+total/1000+".gz");//(outfile+total+".gz");
		    GZIPOutputStream gzo = new GZIPOutputStream(fou);
		    OutputStreamWriter ouw = new OutputStreamWriter(gzo);
		    BufferedWriter bw = new BufferedWriter(ouw);
		    
			set.clear();
			
			c = 0;
			while(c<total){
				i=(int) (Math.random()*num);
				while(set.contains(i)) 
					i=(int) (Math.random()*num);
				
				
				set.add(i);
				int j = lines[i].indexOf(",");
				lines[i]=lines[i].substring(j);
				lines[i]=c+lines[i];
				c++;
				bw.write(lines[i]);
				bw.newLine();
				//System.out.println(lines[i]);
			}
			
			bw.flush();
			bw.close();
		//}
		
		
	}
  */
	
	public static void main(String[] args)throws Exception{
		
		GZIPInputStream gzis = new GZIPInputStream(new FileInputStream("reorder_GN1800.gz"));
	    InputStreamReader xover = new InputStreamReader(gzis);
	    BufferedReader is = new BufferedReader(xover);
	  

	    int num = 0;

	    FileOutputStream fou = new FileOutputStream("reorder_GN1800.txt");
	    OutputStreamWriter ouw = new OutputStreamWriter(fou);
	    BufferedWriter bw = new BufferedWriter(ouw);
	    	    
	    
	    String line;
		while ( (line=is.readLine()) != null) {

		bw.write(line);
		bw.newLine();
		num++;

		}
	
		bw.flush();
		bw.close();
		System.out.println(num);
	}
  		
}		


