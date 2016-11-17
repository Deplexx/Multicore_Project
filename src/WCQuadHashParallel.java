package wordcloud;

import java.util.StringTokenizer;

/**
 * Runnable Instance to calculate the word frequencies for QuadHashTable.
 */
public class WCQuadHashParallel implements Runnable{
	private final String buffer;
    private final QuadHashTable counts;
    /**
     * Initializes buffer with the String value passed in and stores
     * the HashTable to update.
     * 
     * @param buffer - String to count word frequencies for 
     * @param counts - QuadHashTable instance
     */
    public WCQuadHashParallel(String buffer, 
                             QuadHashTable counts) {
        this.counts = counts;
        this.buffer = buffer;
    }    

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     * 
     * @param q - String to look through
     */
    private void updateCount(String q) {
    	if(!StringCleaner.checkValid(q))
    		return;
    	FineSet cnt;
        if (!counts.contains(q)) {
    		if(counts.get(q) == null){ // ensure noone has it initialized
        		FineSet newSet = new FineSet();
        		counts.insert(q, newSet);
    		}

        }
        //System.out.println("Insert Successful");
        cnt = counts.get(q);
        if(cnt == null){
        	updateCount(q);
        	return;
        }
        
        cnt.increment();
    } 

    /**
     * Tokensizes the String List and updates the count for each word.
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
