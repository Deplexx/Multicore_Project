package wordcloud;

import java.util.StringTokenizer;

public class WCQuadHashParallel implements Runnable{
	private final String buffer;
    private final QuadHashTable counts;
    private final static String DELIMS = " :;,.{}()\t\n";

    public WCQuadHashParallel(String buffer, 
                             QuadHashTable counts) {
        this.counts = counts;
        this.buffer = buffer;
    }    

    /**
     * Updates the count for each number of words.  Uses optimistic
     * techniques to make sure count is updated properly.
     */
    private void updateCount(String q) {
    	FineSet cnt;
        if (!counts.contains(q)) {
    		if(counts.get(q) == null){ // ensure noone has it initialized
        		FineSet newSet = new FineSet();
        		counts.insert(q, newSet);
    		}

        }
        //System.out.println("Insert Successful");
        cnt = counts.get(q);
        if(cnt == null)
        	System.out.println("Cnt is null");
        
        cnt.increment();
    } 

    public void run() {
        StringTokenizer st = new StringTokenizer(buffer,DELIMS);
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
