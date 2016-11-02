package wordcloud;

import twitter4j.Paging;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.ArrayList;
import java.util.List;

public class TwitterAccess {
	Twitter twitter;
	List<Status> current_status;
	public TwitterAccess(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("BupgxpMGWJVGZbFCbRTxh8gc2")
		  .setOAuthConsumerSecret("KNvLqYhnU16sr16iqv6XrR8utD4hSwYtdT9EwTjDJCjCoEV2pe")
		  .setOAuthAccessToken("2779499479-kaQXGJHK4hx45SoapkueKEe4N3PshZeERcJmj5n")
		  .setOAuthAccessTokenSecret("l7EL6TCk1cJ1zdGf4DzzK41v0rR2HLZBfT7raR3AfIa8m");
		this.twitter = new TwitterFactory(cb.build()).getInstance();
		current_status = null;
	}
	
	/*
	 * Stores 20 of the user's most recent tweets.
	 */
	public void storeUserTimeline (String username) throws TwitterException {
		this.current_status = this.twitter.getUserTimeline(username);
	}
	
	/*
	 * Stores up to num of user tweets (May be a lot less depending on Twitter capacity).
	 */
	public void storeUserTimeline (String username, int num) throws TwitterException {
		this.current_status = this.twitter.getUserTimeline(username, new Paging(1,num));
	}
	
	/*
	 * Stores numberOfTweets amount of tweets corresponding to the keyword. Keyword is anything
	 * that can be written into search to find tweets in Twitter.com 
	 */
	public void storeQuery (String keyword, int numberOfTweets) {
	    Query query = new Query(keyword);
	    long lastID = Long.MAX_VALUE;
	    this.current_status = new ArrayList<Status>();
	    while (this.current_status.size () < numberOfTweets) {
	      if (numberOfTweets - this.current_status.size() > 100)
	        query.setCount(100);
	      else 
	        query.setCount(numberOfTweets - this.current_status.size());
	      try {
	        QueryResult result = twitter.search(query);
	        /*for (Status status : result.getTweets() ) {
	        	if( status.getText().startsWith("RT") ){
	        		this.current_status.add( status );
	        	}
	        }*/
	        this.current_status.addAll(result.getTweets());
	        for (Status t: this.current_status) 
	          if(t.getId() < lastID) 
	              lastID = t.getId();

	      }

	      catch (TwitterException te) {
	        System.out.println("Couldn't connect: " + te);
	      }; 
	      query.setMaxId(lastID-1);
	    }
	}
	
	/*
	 * Gets the user screen name from the first list. Only useful is storing usertimeline.
	 */
	public String getUserScreenName(){
		if(current_status == null)
			return "";
		
		return current_status.get(0).getUser().getScreenName();
	}
	
	/*
	 * Returns a list of all users from statuses. Useful for stored queries.
	 */
	public List<String> getAllUsers(){
		List<String> result = new ArrayList<String>();
		for(Status status: this.current_status) {
			result.add(status.getUser().getScreenName());
		}
		return result;
	}
	
	/*
	 * Returns a string of all the tweets.
	 */
	public List<String> getStoredStrings(){
		List<String> result = new ArrayList<String>();
		for( Status status: this.current_status ) {
			result.add(status.getText());
		}
		return result;
	}
	
	/*
	 * Returns a list of stored statuses. Statuses store a lot of info which will need to be
	 * retracted.
	 */
	public List<Status> getStoredStatuses(){
		return this.current_status;
	}
}
