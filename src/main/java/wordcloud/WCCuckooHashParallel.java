package wordcloud;

import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Vijay on 11/9/16.
 */
public class WCCuckooHashParallel implements Runnable {
    private final String buffer;
    private final CuckooHashMap map;
    private final static String DELIMS = " :;,.{}()\t\n";
//    public static ReentrantLock lock = new ReentrantLock();

    public WCCuckooHashParallel(String buffer, CuckooHashMap map) {
        this.buffer = buffer;
        this.map = map;
    }

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
//        lock.lock();
        FineSet fineSet = (FineSet)map.get(q);

        if(fineSet == null){ // ensure noone has it initialized
            fineSet = new FineSet();
            map.put(q, fineSet);
            fineSet = (FineSet)map.get(q);
//            lock.unlock();
        }
        fineSet.increment();
//            map.put(q, fineSet);
//            lock.unlock();
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
