package wordcloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import com.kennycason.kumo.WordFrequency;

public class WCFineHashMap implements WordCount {
	public HashMap<String, FineSet> map;
	ReentrantLock lock;
	
	public WCFineHashMap () {
		map = new HashMap<String, FineSet>();
		this.lock = new ReentrantLock();
	}
	
    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        
        for (String str : strList) {
            pool.submit(new WCFineHashParallel(str,this.map, this.lock));
        }
        
        pool.shutdown();
        try {
            pool.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Pool interrupted!");
            System.exit(1);
        }
    }
    
    public List<WordFrequency> toWordFrequency(){
    	List<WordFrequency> wf = new ArrayList<WordFrequency>();
    	
    	for (Map.Entry<String,FineSet> entry : this.map.entrySet()) {
            WordFrequency temp = new WordFrequency(entry.getKey(), entry.getValue().value);
            wf.add(temp);
        }
    	return wf;
    }
    
    public void printWordCount(){
        int total = 0;
    	for (Map.Entry<String,FineSet> entry : this.map.entrySet()) {
            int count = entry.getValue().value;
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("Fine Hash Map Total words: " + total);
    }
}
