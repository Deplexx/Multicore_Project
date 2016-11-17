package wordcloud;

import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentMap;

/**
 * Runnable for the Concurrent Hash Map for WordCount. Goes through the String
 * provided and updates the ConcurrentMap passed in.
 *
 */
public class WCConcHashParallel implements Runnable {
    private final String buffer;
    private final ConcurrentMap<String,Integer> counts;

    public WCConcHashParallel(String buffer, 
                             ConcurrentMap<String,Integer> counts) {
        this.counts = counts;
        this.buffer = buffer;
    }    

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
    	if(!StringCleaner.checkValid(q))
    		return;
        Integer oldVal, newVal;
        Integer cnt = counts.get(q);
        if (cnt == null) {
            oldVal = counts.put(q, 1);
            if (oldVal == null) return;
        }
        do {
            oldVal = counts.get(q);
            newVal = (oldVal == null) ? 1 : (oldVal + 1);
        } while (!counts.replace(q, oldVal, newVal));
    } 

    /**
     * Tokenizes the string stored using the delimiter and 
     */
    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,StringCleaner.DELIMS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            updateCount(token);
        }
    } 
}
