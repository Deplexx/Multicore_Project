package wordcloud;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

public class WCFineHashParallel implements Runnable{
	private final String buffer;
    private final HashMap<String,FineSet> counts;
    private final static String DELIMS = " :;,.{}()\t\n";
    private final ReentrantLock lock;

    public WCFineHashParallel(String buffer, 
                             HashMap<String, FineSet> counts,
                             ReentrantLock maplock) {
        this.counts = counts;
        this.buffer = buffer;
        this.lock = maplock;
    }    

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
        FineSet cnt = counts.get(q);
        
        if (cnt == null) {
        	lock.lock();
    		if(counts.get(q) == null){ // ensure noone has it initialized
        		FineSet newSet = new FineSet();
        		counts.put(q, newSet);
    		}
    		lock.unlock();
        }
        
        cnt = counts.get(q);
        cnt.increment();
    } 

    /**
     * Main task : tokenizes the given buffer and counts words. 
     */
    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,DELIMS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            //System.out.println("updating count for "+token);
            updateCount(token);
        }
    } 
}
