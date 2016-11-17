package wordcloud;

import java.util.List;

import com.kennycason.kumo.WordFrequency;

/**
 * Simple interface to allow keep unity among all WordCount classes. The word count classes needs a way to
 * store the WordCount within the object (via some form of HashMap presumably). Then, we need a way to
 * retrieve a list of WordFrequency which is just a class that stores a String and num, representing the
 * key and frequency of the key. For debug, the class must also output the total frequency of words stored.
 * Finally, need some way to distinguish the different WordCounts
 */
public interface WordCount {
	/**
	 * WordCount runs through the strList using the desired choice of storage (HashMap, etc) across multiple
	 * threads and calculates the frequency. These threads all update the same hashmap.
	 * 	
	 * @param strList - List of String to go through
	 * @param numThreads - Number of threads to divide the task into
	 * @throws java.io.IOException
	 */
	public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException;
	/**
	 * WordCount converts the storage into a list of WordFrequency as defined by kumo. This is used by Kumo
	 * to output a word cloud.
	 * 
	 * @return - a list of word frequency gathered through {@link #storeWordCount(List, int)}
	 */
	public List<WordFrequency> toWordFrequency();
	/**
	 * Goes through the storage to print the total frequency of all words. Used for debugging purposes to 
	 * determine if a specific WordCount was able to store all of the words.
	 * 
	 * @return - Number of total frequency.
	 */
	public int printWordCount();
	/**
	 * Override the toString method to distinguish between the WordCounts.
	 * 
	 * @return - new name of the WordCount.
	 */
	@Override
	public String toString();
}
