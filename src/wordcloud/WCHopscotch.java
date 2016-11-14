package wordcloud;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.kennycason.kumo.WordFrequency;

public class WCHopscotch implements WordCount {
    public ConcurrentHopscotchHashMap<String,Integer> map;
    
    public WCHopscotch(int concLvl) {
        this.map = new ConcurrentHopscotchHashMap<String,Integer>(1000, concLvl);
    }

    public void storeWordCount(List<String> strList, int numThreads) throws java.io.IOException {
    	ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> flist = new ArrayList<Future<?>>();
        
        for (String str : strList) {
            Future<?> f = pool.submit(new WCHopscotchParallel(str,this.map));
            flist.add(f);
        }
        
        pool.shutdown();
        try {
            pool.awaitTermination(1,TimeUnit.DAYS);
        } catch (InterruptedException e) {
            System.out.println("Pool interrupted!");
            System.exit(1);
        }
        
        //for (Future<?> f : flist) {
        	try {
    		   flist.get(0).get();
    		} catch (ExecutionException ex) {
    		   ex.getCause().printStackTrace();
    		} catch (InterruptedException e) {
				e.printStackTrace();
			}
        //}
    }
    
    public List<WordFrequency> toWordFrequency(){
    	List<WordFrequency> wf = new ArrayList<WordFrequency>();
    	
    	for (Map.Entry<String,Integer> entry : this.map.entrySet()) {
            WordFrequency temp = new WordFrequency(entry.getKey(), entry.getValue());
            wf.add(temp);
        }
    	return wf;
    }
    
    public int printWordCount(){
        int total = 0;
    	for (Map.Entry<String,Integer> entry : this.map.entrySet()) {
            int count = entry.getValue();
            //System.out.format("%-30s %d\n",entry.getKey(),count);
            total += count;
        }
        System.out.println("Hopscotch Hash Total words: " + total);        
        return total;
    }
    
    @Override
    public String toString() {
    	return "Hopscotch";
    }
}
