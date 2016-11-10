package wordcloud;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.PixelBoundryBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import twitter4j.Status;
import twitter4j.TwitterException;

public class WordCloudDriver {
	public static void main (String args[]) throws IOException {
		int starTime, endTime;
		long start;
		
		
		List<Status> statuses;
		List<String> messages;
		TwitterAccess ta = new TwitterAccess();
		WCConcHash wc = new WCConcHash();
		WCFineHashMap fh = new WCFineHashMap();
		start = System.currentTimeMillis();
		ta.storeQuery("hillary", 400);
		System.out.println("Twitter Fetch runtime: " + (System.currentTimeMillis() - start) + "ms");
		/*
		statuses = ta.getStoredStatuses();
		for (Status status : statuses) {
			System.out.println("@" + status.getUser().getScreenName() + "-" + status.getText());
		}
		*/
		
		messages = ta.getStoredStrings();
		
		try {
			start = System.currentTimeMillis();
			wc.storeWordCount(messages, 4);
			System.out.println("Conc Hash Map runtime: " + (System.currentTimeMillis() - start) + "ms");
			//wc.printWordCount();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			start = System.currentTimeMillis();
			fh.storeWordCount(messages, 4);
			System.out.println("Fine Hash Map runtime: " + (System.currentTimeMillis() - start) + "ms");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		wc.printWordCount();
		fh.printWordCount();
		// wc.printWordCount();
		//List<WordFrequency> wordFrequencies = fh.toWordFrequency();
		
		

		/*start = System.currentTimeMillis();
		final Dimension dimension = new Dimension(600, 515);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new PixelBoundryBackground(Thread.currentThread().getContextClassLoader().getResourceAsStream("hilary.png")));
		//wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.WHITE, Color.BLUE, 30 , 30));
		//wordCloud.setFontScalar( new SqrtFontScalar(10, 40));
		wordCloud.setFontScalar(new LinearFontScalar(10, 40));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("hilaryoutlin.png");

		System.out.println(System.currentTimeMillis() - start);
		
		
		/*
		final Dimension dimension = new Dimension(600, 60x	0);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new CircleBackground(300));
		wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setFontScalar(new SqrtFontScalar(10, 40));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("potus.png");
		*/
	}
}
