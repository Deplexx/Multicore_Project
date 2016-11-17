package wordcloud;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Runnable for Fine Hash Map. Handles the insert lock.
 */
public class WCFineHashParallel implements Runnable{
	private final String buffer;
    private final HashMap<String,FineSet> counts;
    private final ReentrantLock lock;

    /**
     * Initializes the runnable.
     * 
     * @param buffer - buffer to count words from
     * @param counts - HashMap pointer
     * @param maplock - Lock to synchronize inserts
     */
    public WCFineHashParallel(String buffer, 
                             HashMap<String, FineSet> counts,
                             ReentrantLock maplock) {
        this.counts = counts;
        this.buffer = buffer;
        this.lock = maplock;
    }    

    /**
     * Updates the count of each word. First checks to see if the key already
     * exists. If not, then lock up the table and try to insert.
     * 
     * @param q - String to update count of
     */
    private void updateCount(String q) {
    	if(!StringCleaner.checkValid(q))
    		return;
        FineSet cnt = counts.get(q);
        
        if (cnt == null) {
        	lock.lock();
        	try{
        		if(counts.get(q) == null){ // ensure noone has it initialized
        			FineSet newSet = new FineSet();
        			counts.put(q, newSet);
        		}
        	} finally {
        		lock.unlock();
        	}
        }
        
        cnt = counts.get(q);
        cnt.increment();
    } 

    /**
     * Tokenize the buffer and send each to {@link #updateCount(String)}
     */
    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,StringCleaner.DELIMS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            //System.out.println("updating count for "+token);
            updateCount(token);
        }
    } 
}
