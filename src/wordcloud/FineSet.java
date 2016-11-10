package wordcloud;

import java.util.concurrent.locks.ReentrantLock;

public class FineSet {
	ReentrantLock lock;
	int value;
	public FineSet () {
		this.lock = new ReentrantLock();
		this.value = 0;
	}
	public void increment(){
		this.lock.lock();
		this.value++;
		this.lock.unlock();
	}
}