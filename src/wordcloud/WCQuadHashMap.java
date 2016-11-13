package wordcloud;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.kennycason.kumo.WordFrequency;

class QuadHashEntry {
	public String key;
	public FineSet val;
	QuadHashEntry (String str, FineSet set) {
		key = str;
		val = set;
	}
}

class QuadHashTable
{    
    private int currentSize, maxSize;       
    private String[] keys;   
    private FineSet[] vals;
    //private int ctr;
    private ReentrantLock lock;
 
    /** Constructor **/
    QuadHashTable(int capacity) {
        currentSize = 0;
        //ctr = 0;
        maxSize = capacity;
        keys = new String[maxSize];
        vals = new FineSet[maxSize];
        lock = new ReentrantLock();
    }  
 
    /** Function to check if hash table contains a key **/
    public boolean contains(String key) 
    {
        return get(key) !=  null;
    }
 
    /** Function to get hash code of a given key **/
    private int hash(String key) 
    {
        return key.hashCode() % maxSize;
    }    
    
    /** Function to resize Hash Map **/
    public void resize(){
    	System.out.println("Resizing");
    	int loop_size = maxSize;
    	maxSize*=2;
    	String[] temp_keys = new String[maxSize];
    	FineSet[] temp_vals = new FineSet[maxSize];
    	for(int i = 0; i < loop_size; ++i){
    		temp_keys[i] = keys[i];
    		temp_vals[i] = vals[i];
    	}
    	keys = temp_keys;
    	vals = temp_vals;
    }
 
    /** Function to insert key-value pair **/
    public void insert(String key, FineSet val) {
        int tmp = hash(key);
        if(tmp < 0)
        	tmp = -tmp;
        int i = tmp, h = 1;

        lock.lock();
        //ctr++;
        try {
        	if(currentSize == maxSize) {
            	resize();
            }
            if(contains(key)) {
            	return;
            }
	        do
	        {
	            if (keys[i] == null)
	            {
	                keys[i] = key;
	                vals[i] = val;
	                currentSize++;
	                return;
	            }
	            if (keys[i].equals(key)) 
	            { 
	                vals[i] = val; 
	                return; 
	            }            
	            i = (i + h * h++) % maxSize;            
	        } while (i != tmp);
        } finally {
        	lock.unlock();
        }
    }
 
    /** Function to get value for a given key **/
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
        }
        return null;
    }
    
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

public class WCQuadHashMap implements WordCount {
	private QuadHashTable map;
	
	public WCQuadHashMap(){
		map = new QuadHashTable(3500);
	}
	
    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        
        for (String str : strList) {
            pool.submit(new WCQuadHashParallel(str,this.map));
        }
        
        pool.shutdown();
        try {
            pool.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Pool interrupted!");
            System.exit(1);
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
        System.out.println("Quad Hash Map Total words: " + total);
        return total;
    }
    
    @Override
    public String toString(){
    	return "Quad";
    }
}