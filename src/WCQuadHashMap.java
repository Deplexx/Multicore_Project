package wordcloud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.kennycason.kumo.WordFrequency;

/**
 * QuadHashEntry class of the Quad Hash Table. Simply, stores a key and a value.
 */
class QuadHashEntry {
	public String key;
	public FineSet val;
	
	/**
	 * Initialize the entry to str and FineSet passed.
	 * 
	 * @param str - key
	 * @param set - value
	 */
	QuadHashEntry (String str, FineSet set) {
		key = str;
		val = set;
	}
}

/**
 * Implementation of Hash Table using quadratic probing. Detail on implementation through insert method. 
 * 
 */
class QuadHashTable
{    
	/**
	 * Values required for the implementation of the QuadHashTable. 
	 * 
	 * Currently using a lock so that we can lock up the Hash Table when we have to insert.
	 */
    private int currentSize, maxSize;       
    private String[] keys;   
    private FineSet[] vals;
    //private int ctr;
    private ReentrantLock lock;
 
    /**
     * Initializes the QuadHashTable with given capacity.
     * 
     * @param capacity - initial capacity of the QuadHashTable
     */
    QuadHashTable(int capacity) {
        currentSize = 0;
        //ctr = 0;
        maxSize = capacity;
        keys = new String[maxSize];
        vals = new FineSet[maxSize];
        lock = new ReentrantLock();
    }  
 
    /**
     * Checks to see if the key exists within the table.
     * 
     * @param key - key
     * @return - true/false
     */
    public boolean contains(String key) 
    {
        return get(key) !=  null;
    }
 
    /** 
     * Returns a hash of the key by calling java hashCode and wrapping it around maxSize.
     *
     * @param key - key
     * @return - hash of key
     */
    private int hash(String key) 
    {
        return key.hashCode() % maxSize;
    }    
    
    /**
     * Returns a hash of the key by calling java hashcode and wrapping it around input max.
     * 
     * @param key - key
     * @param max - max to wrap around
     * @return - hash of key
     */
    private int hash(String key, int max)
    {
    	return key.hashCode() % max;
    }
    
    /**
     * Self Insert, used by Resize, inserts the value into the new arrays passed in. This will always
     * work because resize has created these arrays to be bigger than the original arrays.
     * 
     * @param key - key
     * @param val - FineSet value
     * @param key_set - Array of keys
     * @param fine_set - Array of FineSet
     * @param max - new maxSize
     */
    private void self_insert(String key, FineSet val, String[] key_set, FineSet[] fine_set, int max) {
    	int tmp = hash(key, max);
        if(tmp < 0)
        	tmp = -tmp;
        int i = tmp, h = 1;
        do
        {
            if (key_set[i] == null)
            {
                key_set[i] = key;
                fine_set[i] = val;
                return;
            }
            if (key_set[i].equals(key)) 
            { 
                fine_set[i] = val; 
                return; 
            }            
            i = (i + h * h++) % max;            
        } while (i != tmp);
    }
    
    /**
     * Resizes the HashTable by increasing the maxSize by two. Creates two temporary arrays
     * to store new keys and FineSets. Then, calls {@link #self_insert(String, FineSet, String[], FineSet[], int)}
     * to store the new keys/FineSet pair into the newly created arrays. 
     */
    public void resize(){
    	int loop_size = maxSize;
    	int temp_maxSize = maxSize*2;
    	
		String[] temp_keys = new String[temp_maxSize];
    	FineSet[] temp_vals = new FineSet[temp_maxSize];
    	for(int i = 0; i < loop_size; ++i){
    		// found existing key
    		if(keys[i] != null) {
    			self_insert(keys[i], vals[i], temp_keys, temp_vals, temp_maxSize);
    		}
    	}
    	keys = temp_keys;
    	vals = temp_vals;
    	maxSize = temp_maxSize;
    }
 
    /** 
     * Normal insert method that can be called from the outside to insert a new key/value
     * pair into the Hash Table. Firstly, locks up the hash table so we don't have two
     * threads trying to do it at the same thing. Then, if the key is already within the
     * HashTable, then simply return (Case if multiple want to insert the same thing).
     * 
     * Then, if the table is filled, calls {@link #resize()}. 
     * 
     * Once we're all ready, look for the next available location for the key using quadratic
     * probing. This will always be able to find a hole due to the guardbands.
     * 
     * @param key - key
     * @param val - FineSet
     */
    public void insert(String key, FineSet val) {
    	int tmp = hash(key);
        if(tmp < 0)
        	tmp = -tmp;
        /* initial value */
        int i = tmp, h = 1;
        lock.lock();
        try {
            if(contains(key)) {
            	return;
            }
        	if(currentSize == maxSize - 1) {
            	resize();
            }
	        do
	        {
	        	/* Found Empty Slot */
	            if (keys[i] == null) {
	                keys[i] = key;
	                vals[i] = val;
	                currentSize++;
	                return;
	            }
	            /* Quadratic probe for next location */
	            i = (i + h * h++) % maxSize;            
	        } while (i != tmp);
        } finally {	
        	lock.unlock();
        }
    }
 
    /** 
     * Looks through the Table for the key and returns the value if found.
     * 
     * @param key - key
     * @return - value if found, null otherwise
     */
    public FineSet get(String key) 
    {
        int i = hash(key), h = 1;
        if(i < 0)
        	i = -i;
        while (keys[i] != null)
        {
            if (keys[i].equals(key))
                return vals[i];
            i = (i + h * h++) % maxSize;
            if(i < 0)
            	i = -i;
        }
        return null;
    }
    
    /**
     * Returns a set of QuadHashEntry by looking through the table for filled slots.
     * 
     * @return set of QuadHashEntry.
     */
    public List<QuadHashEntry> entrySet(){
    	List<QuadHashEntry> result = new ArrayList<QuadHashEntry>();
    	for(int i = 0; i < maxSize; ++i){
    		if(keys[i] != null){
    			result.add(new QuadHashEntry(keys[i], vals[i]));
    		}
    	}
    	//System.out.println("Found " + result.size() + "Different Words. Should be: " + ctr);
    	return result;
    }
}

/**
 * QuadHashMap implementation of WordCount using a QuadHashTable. 
 */
public class WCQuadHashMap implements WordCount {
	private QuadHashTable map;
	private int initSize;
	
	/**
	 * Initialize Quad Hash Map. 
	 * 
	 * @param sizeHash - size of the quad hash map initially.
	 */
	public WCQuadHashMap(int sizeHash){
		initSize = sizeHash;
		map = new QuadHashTable(sizeHash);
	}
	
    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> flist = new ArrayList<Future<?>>();
        
        for (String str : strList) {
            Future<?> f = pool.submit(new WCQuadHashParallel(str,this.map));
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
    	
    	for (QuadHashEntry entry : this.map.entrySet()) {
            WordFrequency temp = new WordFrequency(entry.key, entry.val.value);
            wf.add(temp);
        }
    	return wf;
    }
    
    public int printWordCount(){
        int total = 0;
    	for (QuadHashEntry entry : this.map.entrySet()) {
            int count = entry.val.value;
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("Quad Hash Map " + initSize + " Total words: " + total);
        return total;
    }
    
    @Override
    public String toString(){
    	return "QP" + initSize;
    }
}