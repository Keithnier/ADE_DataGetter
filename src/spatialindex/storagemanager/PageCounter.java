package spatialindex.storagemanager;

public class PageCounter {

	private int id;
	private int count;
	
	public PageCounter(int i){
		id = i;
		count = 1;
	}
	public void increaseCounter(){
		count++;
	}
	public int getID(){
		return id;
	}
	public int getCounter(){
		return count;
	}
}
