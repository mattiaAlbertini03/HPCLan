package semanticanalysis;

import java.util.ArrayList;
import java.util.HashMap;

public class ConstTable {
	private final ArrayList<HashMap<String,Integer>>  const_table ;
	
	public ConstTable() {
		const_table = new ArrayList<HashMap<String,Integer>>() ;
	}
	
	public Integer lookup(String id) {
		int n = const_table.size() - 1 ;
		boolean found = false ;
		Integer T = null ;
		while ((n >= 0) && !found) {
			HashMap<String,Integer> H = const_table.get(n) ;
			T = H.get(id) ;
			if (T != null) found = true ;
			else n = n-1 ;
		}
		return T ;
	}

	public void add(HashMap<String,Integer> H) {
		const_table.add(H) ;
	}
	
	public void remove() {
		int x = const_table.size() ;
		const_table.remove(x-1) ;
	}
	
	public void insert(String id, Integer num) {
		int n = const_table.size() - 1 ;
		HashMap<String,Integer> H = const_table.get(n) ;
		const_table.remove(n) ;
		H.put(id,num) ;
		const_table.add(H) ;
	}

}
