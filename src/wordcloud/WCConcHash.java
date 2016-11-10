package wordcloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kennycason.kumo.WordFrequency;

public class WCConcHash implements WordCount {
    public ConcurrentMap<String,Integer> map;
    
    public WCConcHash (){
        this.map = new ConcurrentHashMap<String,Integer>();
    }

    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        
        for (String str : strList) {
            pool.submit(new WCConcHashParallel(str,this.map));
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
    	
    	for (Map.Entry<String,Integer> entry : this.map.entrySet()) {
            WordFrequency temp = new WordFrequency(entry.getKey(), entry.getValue());
            wf.add(temp);
        }
    	return wf;
    }
    
    public void printWordCount(){
        int total = 0;
    	for (Map.Entry<String,Integer> entry : this.map.entrySet()) {
            int count = entry.getValue();
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("Conc Hash Map Total words: " + total);
    }
}
