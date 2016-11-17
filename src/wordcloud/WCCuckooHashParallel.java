package wordcloud;

import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Vijay on 11/9/16.
 */
public class WCCuckooHashParallel implements Runnable {
    private final String buffer;
    private final CuckooHashMap map;
    private final static String DELIMS = " :;,.{}()\t\n";

    public WCCuckooHashParallel(String buffer, CuckooHashMap map) {
        this.buffer = buffer;
        this.map = map;
    }

    /**
     * Updates the count for each number of words.
     */
    private void updateCount(String q) {
        boolean noPut = true;
        FineSet fineSet = (FineSet)map.get(q);
        if(fineSet == null){ // ensure noone has it initialized
            noPut = false;
            fineSet = new FineSet();
            fineSet = (FineSet) map.put(q, fineSet);
        }

        try {
            fineSet.increment();
        } catch(Exception e) {
            updateCount(q);
        }
    }

    /**
     * Main task : tokenizes the given buffer and counts words.
     */
    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,DELIMS);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            updateCount(token);
        }
    }
}