package wordcloud;

import java.util.List;

import com.kennycason.kumo.WordFrequency;

public interface WordCount {
	public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException;
	public List<WordFrequency> toWordFrequency();
	public void printWordCount();
}
