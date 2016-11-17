package wordcloud;

/**
 * Simple fine set solution for the word frequency. The hash map can contain a key and this object to synchronously increment.
 */
public class FineSet {
	int value;
	/**
	 * Initialize to 0.
	 */
	public FineSet () {
		this.value = 0;
	}
	/**
	 * Increment value by 1 synchronously.
	 */
	public synchronized void increment(){
		this.value++;
	}
}