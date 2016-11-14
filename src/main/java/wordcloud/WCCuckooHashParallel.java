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
    public static ReentrantLock lock = new ReentrantLock();

    public WCCuckooHashParallel(String buffer, CuckooHashMap map) {
        this.buffer = buffer;
        this.map = map;
//        lock = new ReentrantLock();
    }

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
        lock.lock();
        if(map.get(q) == null){ // ensure noone has it initialized
            map.put(q, 1);
            lock.unlock();
        } else {
            int count = (Integer)map.get(q);
//            System.out.println("count of word is " + count);
            count += 1;
            map.remove(q);
            map.put(q, count);
            lock.unlock();
        }
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
