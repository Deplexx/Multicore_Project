package wordcloud;

import com.kennycason.kumo.WordFrequency;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Cuckoo Hash Map
 * 
 * Implementation of Map interface using Cuckoo Hashing.
 * Using two hash functions instead of single hash function to determine the
 * possible location of an entry in the map. An entry is allocated in one of the
 * two possible location of for the key, kicking out any previous entry from the
 * position if present. The key thrown out now imitates behavior of the new
 * entry and tries to accomodate itself in one of the two possible locations.
 * This process may result in an infinite loop, in which case, the underlying
 * data structure is re-hashed and all the keys from the old data structure is
 * re-mapped to their preferred locations in the new data structure.
 * 
 * In this simplified implementation, we take a array data structure, each key
 * value pair represented by an Entry<K, V>. Two hash functions hash1 and hash2
 * are used to determine index position to insert an entry to table.
 * 
 */

class CuckooHashMap<K, V> extends AbstractMap<K, V> implements
		Map<K, V> {

	static final int DEFAULT_INITIAL_CAPACITY = 50000;
	static final int MAXIMUM_CAPACITY = 1 << 30;
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	private int currentCapacity;

	/* Test main */
	public static void main(String[] args) {
		Map<Integer, String> map = new CuckooHashMap<Integer, String>();
		int itemCount = 25;
		for (int i = 0; i < itemCount; i++) {
			Integer key = i;
			String val = "Value_" + i;
			map.put(key, val);
		}

		System.out.println(map.get(1));

		for (String v : map.values()) {
			System.out.println(v);
		}
	}
	
	static class DefaultHashFunction<T> implements HashFunction<T> {
		private static final Random ENGINE = new Random();
		private int rounds;

		public DefaultHashFunction() {
			this(1);
		}

		public DefaultHashFunction(int rounds) {
			this.rounds = rounds;
		}

		public int hash(Object key, int limit) {
			ENGINE.setSeed(key.hashCode());
			int h = ENGINE.nextInt(limit);
			for (int i = 1; i < this.rounds; i++) {
				h = ENGINE.nextInt(limit);
			}

			return h;
		}
	}

	static class Entry<K, V> implements Map.Entry<K, V> {
		final K key;
		V value;

		Entry(K k, V v) {
			value = v;
			key = k;
		}

		public final boolean equals(Object o) {
			if (!(o instanceof Map.Entry))
				return false;
			Map.Entry e = (Map.Entry) o;
			Object k1 = getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2))) {
				Object v1 = getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
					return true;
			}
			return false;
		}

		public final K getKey() {
			return CuckooHashMap.unmaskNull(key);
		}

		public final V getValue() {
			return value;
		}

		public final int hashCode() {
			return (key == null ? 0 : key.hashCode())
					^ (value == null ? 0 : value.hashCode());
		}

		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}

		public final String toString() {
			return getKey() + "=>" + getValue();
		}
	}

	static interface HashFunction<T> {
		public int hash(Object key, int limit);
	}

	static <T> T maskNull(T key) {
		return key == null ? (T) NULL_KEY : key;
	}

	static <T> T unmaskNull(T key) {
		return (key == NULL_KEY ? null : key);
	}

	transient Entry<K, V>[] table;

	transient int size;

	int threshold;

	final float loadFactor;

	final transient HashFunction<K> hash1;

	final transient HashFunction<K> hash2;

	static final Object NULL_KEY = new Object();

	public CuckooHashMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new Entry[DEFAULT_INITIAL_CAPACITY];
		currentCapacity = DEFAULT_INITIAL_CAPACITY;
		hash1 = new DefaultHashFunction<K>(2);
		hash2 = new DefaultHashFunction<K>(3);
		init();
	}

	public CuckooHashMap(HashFunction<K> h1, HashFunction<K> h2) {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, h1, h2);
	}

	public CuckooHashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public CuckooHashMap(int initialCapacity, float loadFactor) {
		this(initialCapacity, loadFactor, new DefaultHashFunction<K>(2),
				new DefaultHashFunction<K>(3));
	}

	public CuckooHashMap(int initialCapacity, float loadFactor,
			HashFunction<K> h1, HashFunction<K> h2) {
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new Entry[capacity];
		hash1 = h1;
		hash2 = h2;
		init();
	}

	public CuckooHashMap(Map<? extends K, ? extends V> m) {
		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
				DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
	}

	int capacity() {
		return table.length;
	}

	public Set<Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<K, V>> es = new HashSet<Map.Entry<K, V>>();
		for (Entry<K, V> e : table) {
			if (e != null) {
				es.add(e);
			}
		}

		return es;
	}


	public synchronized V get(Object key) {
		//
		int hash = hash(hash1, key);
		Entry<K, V> e = table[hash];
		if (e != null && e.key.equals(key)) {
			return e.value;
		}

		hash = hash(hash2, key);
		e = table[hash];
		if (e != null && e.key.equals(key)) {
			return e.value;
		}
		return null;
	}

	private int hash(HashFunction<K> func, Object key) {
		return func.hash(key, table.length);
	}

	private void init() {
	}

	private boolean insertEntry(Entry<K, V> e) {
		int count = 0;
		Entry<K, V> current = e;
		int index = hash(hash1, current.key);
		while (current != e || count < table.length) {
			Entry<K, V> temp = table[index];
			if (temp == null) {
				table[index] = current;
				return true;
			}

			table[index] = current;
			current = temp;
			if (index == hash(hash1, current.key)) {
				index = hash(hash2, current.key);
			} else {
				index = hash(hash1, current.key);
			}

			++count;
		}

		return false;
	}
/**
	private boolean insertEntry(Entry<K, V> e) {
		int count = 0;
		Entry<K, V>[] newTable = new Entry[currentCapacity];

		for (K key : this.keySet()){
			if(key != null){
				Entry<K,V> epee = new Entry<K,V> (key, get(key));
				insertEntry(epee, newTable);
			}
		}
		if(!insertEntry(e, newTable)) {
			return false;
		}

		table = newTable;
		return true;
	}
**/
	public boolean isEmpty() {
		return size == 0;
	}

	float loadFactor() {
		return loadFactor;
	}

	public V put(K key, V value) {
		return put(key, value, false);
	}

	/**
	 * Don't need to use AbstractMap's containsKey, because Cuckoo hashing ensures its at one of two locations
	 */
	private boolean contains(K key) {
		return get(key) != null;
	}

	private synchronized V put(K key, V value, boolean isRehash) {
		if (contains(key)) {
//			int hash = hash(hash1, k);
//			Object k2;
//			Entry<K, V> e = table[hash];
//			if (e != null && ((k2 = e.key) == k || k.equals(k2))) {
//				e.value = value;
//				return e.value;
//			}
//
//			hash = hash(hash2, k);
//			e = table[hash];
//			if (e != null && ((k2 = e.key) == k || k.equals(k2))) {
//				e.value = value;
//				return e.value;
//			}



//
//			System.out.println("@@@@@@@@@@@@@@ SHOULD NOT REACH HERE");
//			int index = hash(hash1, key);
//			Entry<K, V> e = table[hash]
			return get(key);
		}

		if (insertEntry(new Entry<K, V>((K) key, value))) {
			if (!isRehash) {
				size++;
			}

			return get(key);
		}

		rehash(2 * table.length);
		return put((K) key, value);
	}

	private void rehash(int newCapacity) {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		Entry<K, V>[] oldTable = table;
		int oldCapacity = oldTable.length;
		if (oldCapacity >= MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		Entry<K, V>[] newTable = new Entry[newCapacity];
		table = newTable;
		for (Entry<K, V> e : oldTable) {
			if (e != null) {
				put(e.key, e.value, true);
			}
		}
		currentCapacity = newCapacity;
		threshold = (int) (newCapacity * loadFactor);
	}

	public int size() {
		return size;
	}

}

class CuckooHash implements WordCount {
	private CuckooHashMap<String, FineSet> map;

	public CuckooHash(){
		this.map = new CuckooHashMap<String, FineSet>();
	}

	public void storeWordCount(List<String> strList, int numThreads) throws IOException {
//		System.out.println("words are " + strList);
		ExecutorService pool = Executors.newFixedThreadPool(numThreads);
//		for (String str : strList) {
//			pool.submit(new WCCuckooHashParallel(str, map));
//		}
//
//		pool.shutdown();
//		try {
//			pool.awaitTermination(1, TimeUnit.DAYS);
//		} catch (InterruptedException e) {
//			System.out.println("Pool interrupted!");
//			System.exit(1);
//		}







		List<Future<?>> flist = new ArrayList<Future<?>>();

		for (String str : strList) {
			Future<?> f = pool.submit(new WCCuckooHashParallel(str,this.map));
			flist.add(f);
		}

		pool.shutdown();
		try {
			pool.awaitTermination(1,TimeUnit.DAYS);
		} catch (InterruptedException e) {
			System.out.println("Pool interrupted!");
			System.exit(1);
		}

		for (Future<?> f : flist) {
			try {
				f.get();
			} catch (ExecutionException ex) {
				ex.getCause().printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public List<WordFrequency> toWordFrequency() {
		List<WordFrequency> wf = new ArrayList<WordFrequency>();
		for(Object key : map.keySet()) {
			WordFrequency temp = new WordFrequency((String) key, (Integer) map.get(key).value);
			wf.add(temp);
		}
		return wf;
	}

	public int printWordCount() {
		int total = 0;
		System.out.println("Key set size is : " + map.keySet().size());
		for(Object key : map.keySet()) {
			FineSet f  = map.get(key);
			if(f != null) {
				total += f.value;//f.value;
			}
		}
		System.out.println("Cuckoo Hash Map Total words: " + total);
		return total;


//		int total = 0;
//		for (Map.Entry<String,Integer> entry : this.map.entrySet()) {
//			int count = entry.getValue().value;
//			//System.out.format("%-30s %d\n",entry.getKey(),count);
//			total += count;
//			//ctr += 1;
//		}
//		System.out.println("Fine Hash Map Total words: " + total);
//		return total;
	}

	@Override
	public String toString() {
		return "Cuckoo";
	}
}