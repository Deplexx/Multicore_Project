package wordcloud;

import java.util.concurrent.locks.ReentrantLock;


class WCQuadHashTable
{    
    private int currentSize, maxSize;       
    private String[] keys;   
    private FineSet[] vals;
    private ReentrantLock lock;
 
    /** Constructor **/
    public WCQuadHashTable(int capacity) {
        currentSize = 0;
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
    
    /** **/
    public void resize(){
    	
    }
 
    /** Function to insert key-value pair **/
    public void insert(String key, FineSet val) {
        int tmp = hash(key);
        int i = tmp, h = 1;
        lock.lock();
        if(currentSize == maxSize) {
        	resize();
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
        lock.unlock();
    }
 
    /** Function to get value for a given key **/
    public FineSet get(String key) 
    {
        int i = hash(key), h = 1;
        while (keys[i] != null)
        {
            if (keys[i].equals(key))
                return vals[i];
            i = (i + h * h++) % maxSize;
            System.out.println("i "+ i);
        }            
        return null;
    }     
}