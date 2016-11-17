package wordcloud;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;


public class WordCloudDriver {
	/** Driver logistics **/
	public static boolean SHOW_TOTAL = true;
	public static boolean DRAW_KUMO = false;
	public static boolean SHOW_GRAPH = true;
	
	/** Input/Output **/
	public static int NUM_THREADS = 4;
	public static int NUM_TWEETS = 100;
	public static String TWITTER_SEARCH = "obama";
	public static String INPUT_IMAGE = "obama.png";
	public static String OUTPUT_IMAGE = "obama.png";
	public static int[] INPUT_DIM = {600, 515};
	public static Color OUTPUT_COLOR1 = Color.RED;
	public static Color OUTPUT_COLOR2 = Color.WHITE;
	public static Color OUTPUT_COLOR3 = Color.BLUE;
	public static LineGraph chart = new LineGraph( "Dataset vs Time" );
	
	/** Function to output Word Cloud given the list of word frequencies provided
	 * 
	 * @param wordFrequencies : List of Word Frequencies
	 * @throws IOException
	 */
	public static void outputWordCloud(List<WordFrequency> wordFrequencies) throws IOException{
		final Dimension dimension = new Dimension(INPUT_DIM[0], INPUT_DIM[1]);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new PixelBoundryBackground(Thread.currentThread().getContextClassLoader().getResourceAsStream(INPUT_IMAGE)));
		//wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setColorPalette(new LinearGradientColorPalette(OUTPUT_COLOR1, OUTPUT_COLOR2, OUTPUT_COLOR3, 30 , 30));
		//wordCloud.setFontScalar( new SqrtFontScalar(10, 40));
		wordCloud.setFontScalar(new LinearFontScalar(10, 40));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile(OUTPUT_IMAGE);
	}
	
	/** Function to iterate through the WordCount given a list of WordCount, string to
	 * look through and number of threads to user per array. All WordCount should follow
	 * the interface WordCount
	 * 
	 * @param array : Array of initialized WordCounts
	 * @param msg : Array of Strings to go through
	 * @param numThreads : Number of Threads to use to store array
	 * @return : List of Word Frequencies
	 */
	public static List<WordFrequency> iterate_wordcount (List<WordCount> array, List<String> msg){
		long start;
		int runtime;
		
		for(WordCount wc: array) {
			start = System.currentTimeMillis();
			try {
				wc.storeWordCount(msg, NUM_THREADS);
			} catch (IOException e){
				e.printStackTrace();
			}
			runtime =  (int) (System.currentTimeMillis() - start);
			System.out.println(wc.toString() + " Hash run time: " + runtime + "ms");
			chart.addData(wc.toString(), runtime);
		}
		
		if(SHOW_TOTAL) {
			for(WordCount wc: array){
				wc.printWordCount();
			}
		}
		
		return array.get(0).toWordFrequency();
	}
	
	/** Main function wither gathers all of the tweets, gets iterates through them for
	 * timing logistics and wordFrequency, and outputs a wordCloud and Line Graph.
	 * 
	 * Currently does not handle command line arguments/
	 * 
	 * @param args - UNUSED
	 * @throws IOException
	 */
	public static void main (String args[]) throws IOException {
		long start;
		List<String> messages;
		TwitterAccess ta = new TwitterAccess();
		
		start = System.currentTimeMillis();
		ta.storeQuery(TWITTER_SEARCH, NUM_TWEETS);
		System.out.println("Twitter Fetch runtime: " + (System.currentTimeMillis() - start) + "ms");
		messages = ta.getStoredStrings();
		
		/* Create a list of word frequencies for iterate_wordcount */
		List<WordCount> wcList = new ArrayList<WordCount>();
		wcList.add(new WCConcHash());
		wcList.add(new WCFineHashMap());
		wcList.add(new WCQuadHashMap());
		wcList.add(new WCLFChainHash(2048));
		wcList.add(new WCLFChainHash(32));
		wcList.add(new WCHopscotch(4));
		List<WordFrequency> wordFrequencies = iterate_wordcount(wcList, messages);
	
		if(DRAW_KUMO) {
			start = System.currentTimeMillis();
			outputWordCloud(wordFrequencies);
			System.out.println("Kumo runtime: " + (System.currentTimeMillis() - start) + "ms");
		}
		
		if(SHOW_GRAPH) {
			chart.readyGraph("Comparison");
		}
	}
}
