package wordcloud;

import java.util.StringTokenizer;

/**
 * Runnable for the Lock Free Chain Hash Table implementation of WordCount.
 */
public class WCLFChainParallel implements Runnable{
	private final String buffer;
    private final ChainHashTable counts;

    public WCLFChainParallel(String buffer, 
                             ChainHashTable counts) {
        this.counts = counts;
        this.buffer = buffer;
    }    

    /**
     * Checks to see if the value exists, in which case insert it atomically.
     * Then, increment atomically.
     */
    private void updateCount(String q) {
    	if(!StringCleaner.checkValid(q))
    		return;
        if (!counts.contains(q)) {
    		if(counts.get(q) == null){ // ensure noone has it initialized
        		//FineSet newSet = new FineSet();
        		// Keep trying to insert if insert fails.
        		while(!counts.insert(q, 0));
    		}

        }
        //System.out.println("Insert Successful");
        int cnt = counts.get(q).get();
        if(cnt == -1)
        	System.out.println("Cnt is null");
        
        while(counts.get(q).incrementAndGet() == cnt);
    } 

    /**
     * Tokenize and pass to {@link #updateCount(String)}
     */
    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,StringCleaner.DELIMS);
        //System.out.println("Buffer " + buffer);
        while (st.hasMoreTokens()) {
            String token = st.nextToken().toUpperCase();
            //System.out.println("updating count for "+token);
            try {
            	updateCount(token);
            } catch (Error ex) {
    			ex.printStackTrace();
            }
        
        }
    } 
}
