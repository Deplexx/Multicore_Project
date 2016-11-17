package wordcloud;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.kennycason.kumo.WordFrequency;

/**
 * Simple class for the Entry of the Chain Hash Table.
 * Contains a key, a value, and a refernce pointer to the next entry.
 */
class ChainEntry {
	public String key;
	public AtomicInteger val;
	public AtomicReference<ChainEntry> next;
	
	/**
	 * Initialize the Entry with next in chain as null AtomicReference.
	 * 
	 * @param str - key
	 * @param set - value
	 */
	ChainEntry(String str, int set) {
		key = str;
		val = new AtomicInteger();
		next = new AtomicReference<ChainEntry>();
	}
}

/**
 * Lock Free Chain hash table implementation of Hash Table for the purpose of word frequency
 * comparison.
 */
class ChainHashTable {
	private int maxSize;
	private ChainEntry[] list;
	
	/**
	 * Initialize the HashTable with specified capacity. Set all values to null (Sentinel).
	 * 
	 * @param capacity - size of hash table
	 */
	ChainHashTable(int capacity){
		maxSize = capacity;
		
		list = new ChainEntry[capacity];
		
		for(int i = 0; i < capacity; i++){
			list[i] = new ChainEntry(null, 0);
		}
	}
	
	/**
	 * Get a good hash of the key by wrapping around the maxSize and ensuring that it is positive.
	 * 
	 * @param key - key
	 * @return - hash used for table
	 */
    private int hash(String key) 
    {
        int tmp = key.hashCode() % maxSize;
		if(tmp < 0)
			tmp = -tmp;
		return tmp;
    }
    
    /**
     * Allows outside functions to retrieve the size of the table (can actually store infinite keys)
     * 
     * @return
     */
    public int maxSize(){
    	return maxSize;
    }
	
    /**
     * Try to insert the key-set pair into the Hash Table. First, find the hash of the key for this table.
     * Then, find the next open ChainEntry (with a next set to null). Then, use CAS to try to set value.
     *  
     * @param key - key
     * @param val - new value
     * @return true if successfully added or already exists. False otherwise.
     */
	public boolean insert(String key, int val){
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
	
	/**
	 * Gets the hash of the key for the table and looks through the chain to find it.
	 * 
	 * @param key - key
	 * @return the AtomicInteger corresponding to the key if exists. Null otherwise.
	 */
	public AtomicInteger get(String key){
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
	
	/**
	 * Checks to see if the key exists within the table.
	 * 
	 * @param key - key
	 * @return true if exists, false otherwise
	 */
	public boolean contains (String key) {
		return get(key) != null;
	}
	
	/**
	 * Goes through the entire table and all of the chains to get a list of all 
	 * ChainEntry.
	 * 
	 * @return a List of all ChainEntries within the Chain Hash Table.
	 */
	public List<ChainEntry> entrySet() {
		List<ChainEntry> result = new ArrayList<ChainEntry>();
		
		for(int i = 0; i < maxSize; i++) {
			if(list[i] == null)
				continue;
			ChainEntry curr = list[i].next.get();
			
			while(curr != null) {
				result.add(curr);
				curr = curr.next.get();
			}
		}
		
		return result;
	}
}

/**
 * Chain Hash Table implementation of Word Count.
 */
public class WCLFChainHash implements WordCount {
	private ChainHashTable map;
	
	/** 
	 * Initialize the WCLF Chain hash Table with specified size.
	 * 
	 * @param startSize - initial size
	 */
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
            WordFrequency temp = new WordFrequency(entry.key, entry.val.get());
            wf.add(temp);
        }
    	return wf;
    }
    
    public int printWordCount(){
        int total = 0;
    	for (ChainEntry entry : this.map.entrySet()) {
            int count = entry.val.get();
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("LF Chain Hash " + this.map.maxSize() + " Total words: " + total);
        return total;
    }
    
    @Override
    public String toString(){
    	return ("LFC" + this.map.maxSize());
    }
}