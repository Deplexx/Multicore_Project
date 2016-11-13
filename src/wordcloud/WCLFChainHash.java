package wordcloud;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.kennycason.kumo.WordFrequency;

class ChainEntry {
	public String key;
	public FineSet val;
	public AtomicReference<ChainEntry> next;
	
	ChainEntry(String str, FineSet set) {
		key = str;
		val = set;
		next = new AtomicReference<ChainEntry>();
	}
}

class ChainHashTable {
	private int maxSize;
	private ChainEntry[] list;
	
	ChainHashTable(int capacity){
		maxSize = capacity;
		
		list = new ChainEntry[capacity];
		
		for(int i = 0; i < capacity; i++){
			list[i] = new ChainEntry(null, null);
		}
	}
	
    /** Function to get hash code of a given key **/
    private int hash(String key) 
    {
        int tmp = key.hashCode() % maxSize;
		if(tmp < 0)
			tmp = -tmp;
		return tmp;
    }
    
    public int maxSize(){
    	return maxSize;
    }
	
	public boolean insert(String key, FineSet val){
		int indx = hash(key);
		
		ChainEntry newCE = new ChainEntry(key, val);
		ChainEntry curr = list[indx];
		
		while(curr.next.get() != null) {
			// Found Key meaning someone else added it
			if(curr.key != null && curr.key.equals(key)) {
				return true;
			}
			curr = curr.next.get();
		}
		
		return curr.next.compareAndSet(null, newCE);
	}
	
	public FineSet get(String key){
		int indx = hash(key);
		
		ChainEntry curr = list[indx].next.get();
		
		while(curr != null) {
			if(curr.key.equals(key)) {
				return curr.val;
			}
			curr = curr.next.get();
		}
		return null;
	}
	
	public boolean contains (String key) {
		return get(key) != null;
	}
	
	public List<ChainEntry> entrySet() {
		List<ChainEntry> result = new ArrayList<ChainEntry>();
		
		for(int i = 0; i < maxSize; i++) {
			ChainEntry curr = list[i].next.get();
			
			while(curr != null) {
				result.add(curr);
				curr = curr.next.get();
			}
		}
		
		return result;
	}
}

public class WCLFChainHash implements WordCount {
	private ChainHashTable map;
	
	public WCLFChainHash(int startSize){
		map = new ChainHashTable(startSize);
	}
	
    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> flist = new ArrayList<Future<?>>();
        
        for (String str : strList) {
            Future<?> f = pool.submit(new WCLFChainParallel(str,this.map));
            flist.add(f);
        }
        
        pool.shutdown();
        try {
            pool.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Pool interrupted!");
            System.exit(1);
        }
        
        for (Future<?> f : flist) {
        	try {
    		   f.get();
    		} catch (ExecutionException ex) {
    		   ex.getCause().printStackTrace();
    		} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
    
    public List<WordFrequency> toWordFrequency(){
    	List<WordFrequency> wf = new ArrayList<WordFrequency>();
    	
    	for (ChainEntry entry : this.map.entrySet()) {
            WordFrequency temp = new WordFrequency(entry.key, entry.val.value);
            wf.add(temp);
        }
    	return wf;
    }
    
    public int printWordCount(){
        int total = 0;
    	for (ChainEntry entry : this.map.entrySet()) {
            int count = entry.val.value;
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("LF Chain Hash Total words: " + total);
        return total;
    }
    
    @Override
    public String toString(){
    	return ("LFC" + this.map.maxSize());
    }
}