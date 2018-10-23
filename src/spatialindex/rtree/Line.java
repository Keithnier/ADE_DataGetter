package spatialindex.rtree;

public class Line {

	public double a;
	public double b;
	public double _start;
	public double _end;
	public double alpha;
	public int k;
	public int initial_rank;
	public int oid;
	
	Line(double a, double b, double alpha, int k,int rank){
		this.a = a;
		this.b = b;
		this.alpha = alpha;
		this.k =k;
		this.initial_rank = rank;
		
	}
	
    Line(int id, double a, double b){
    	this.oid = id;
    	this.a = a;
		this.b = b;
	}

	public boolean equals(Object obj){
		if(!(obj instanceof Line)) return false;

		return this.oid == ((Line) obj).oid;
    	
    }
}
