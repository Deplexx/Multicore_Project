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

import twitter4j.Status;
import twitter4j.TwitterException;

public class WordCloudDriver {
	public static void main (String args[]) throws IOException {
		List<Status> statuses;
		List<String> messages;
		TwitterAccess ta = new TwitterAccess();
		WordCount wc = new WordCount();
		ta.storeQuery("#ironman", 100);
		
		/*
		statuses = ta.getStoredStatuses();
		for (Status status : statuses) {
			System.out.println("@" + status.getUser().getScreenName() + "-" + status.getText());
		}
		*/
		
		messages = ta.getStoredStrings();
		
		try {
			wc.storeWordCount(messages, 4);
			//wc.printWordCount();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//wc.printWordCount();
		List<WordFrequency> wordFrequencies = wc.toWordFrequency();
		/*for (WordFrequency wf : wordFrequencies) {
			System.out.println(wf.getWord() + wf.getFrequency());
		}*/
		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		
		
		//final List<WordFrequency> wordFrequencies = frequencyAnalyzer.load(getInputStream("text/datarank.txt"));
		
		final Dimension dimension = new Dimension(500, 312);
		final WordCloud wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
		wordCloud.setPadding(2);
		wordCloud.setBackground(new PixelBoundryBackground(Thread.currentThread().getContextClassLoader().getResourceAsStream("ironman.png")));
		wordCloud.setColorPalette(new ColorPalette(new Color(0x4055F1), new Color(0x408DF1), new Color(0x40AAF1), new Color(0x40C5F1), new Color(0x40D3F1), new Color(0xFFFFFF)));
		wordCloud.setFontScalar(new LinearFontScalar(10, 40));
		wordCloud.build(wordFrequencies);
		wordCloud.writeToFile("ironmanout.png");
		
		/*
		final Dimension dimension = new Dimension(600, 600);
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
