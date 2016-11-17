package wordcloud;

import java.util.StringTokenizer;

public class WCHopscotchParallel implements Runnable {
    private final String buffer;
    private final ConcurrentHopscotchHashMap<String,Integer> counts;
    private final static String DELIMS = " :;,.{}()\t\n";

    public WCHopscotchParallel(String buffer, 
    		ConcurrentHopscotchHashMap<String,Integer> counts) {
        this.counts = counts;
        this.buffer = buffer;
    }    

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
        Integer oldVal, newVal;
        Integer cnt = counts.get(q);
        // first case: there was nothing in the table yet
        if (cnt == null) {
            // attempt to put 1 in the table.  If the old
            // value was null, then we are OK.  If not, then
            // some other thread put a value into the table
            // instead, so we fall through
            oldVal = counts.put(q, 1);
            if (oldVal == null) return;
        }
        // general case: there was something in the table
        // already, so we have increment that old value
        // and attempt to put the result in the table.
        // To make sure that we do this atomically,
        // we use concurrenthashmap's replace() method
        // that takes both the old and new value, and will
        // only replace the value if the old one currently
        // there is the same as the one passed in.
        // Cf. http://www.javamex.com/tutorials/synchronization_concurrency_8_hashmap2.shtml 
        do {
            oldVal = counts.get(q);
            newVal = (oldVal == null) ? 1 : (oldVal + 1);
        } while (!counts.replace(q, oldVal, newVal));
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