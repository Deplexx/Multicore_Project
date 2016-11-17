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

/**
 * Simplified version of Twitter Access from twitter4j API.
 * 
 * Provides the means to look up a user timeline or query twitter given a string. This object will allow
 * user the store the result within itself. User can call the different output methods to return in the
 * desired format. 
 */
public class TwitterAccess {
	Twitter twitter;
	List<Status> current_status;
	
	/**
	 * Initializes twitter access given the consumer/access token/secret from Twitter Application Developer
	 * website which allows this program access to twitter API.
	 */
	public TwitterAccess(){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey("")
		  .setOAuthConsumerSecret("")
		  .setOAuthAccessToken("")
		  .setOAuthAccessTokenSecret("");
		this.twitter = new TwitterFactory(cb.build()).getInstance();
		current_status = null;
	}
	
	/**
	 * Store 20 most recent tweets from the given username. 
	 * 
	 * WARNING: Twitter discards old tweets.
	 * 
	 * @param username - name to lookup in twitter
	 * @throws TwitterException 
	 */
	public void storeUserTimeline (String username) throws TwitterException {
		this.current_status = this.twitter.getUserTimeline(username);
	}
	
	/**
	 * Stores num most recent tweets from the given username.
	 * 
	 * WARNING: Twitter discards old tweets. If num > tweets available, will only store max given.
	 * 
	 * @param username - name to lookup in twitter
	 * @param num - number of tweets to look for.
	 * @throws TwitterException
	 */
	public void storeUserTimeline (String username, int num) throws TwitterException {
		this.current_status = this.twitter.getUserTimeline(username, new Paging(1,num));
	}
	
	/**
	 * Stores numberOfTweets amount of tweets corresponding to the keyword. Keyword is anything
	 * that can be written into search to find tweets in Twitter.com 
	 * 
	 * @param keyword - Keyword to search twitter with
	 * @param numberOfTweets - number of tweets to query
	 */
	public void storeQuery (String keyword, int numberOfTweets) throws TwitterException {
	    Query query = new Query(keyword);
	    long lastID = Long.MAX_VALUE;
	    this.current_status = new ArrayList<Status>();
	    while (this.current_status.size () < numberOfTweets) {
	      if (numberOfTweets - this.current_status.size() > 100)
	        query.setCount(100);
	      else 
	        query.setCount(numberOfTweets - this.current_status.size());
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
	      query.setMaxId(lastID-1);
	    }
	}
	
	/**
	 * Gets the user screen name from the first list. Only useful is storing usertimeline.
	 */
	public String getUserScreenName(){
		if(current_status == null)
			return "";
		
		return current_status.get(0).getUser().getScreenName();
	}
	
	/**
	 * Returns a list of all users from statuses. Useful for stored queries.
	 */
	public List<String> getAllUsers(){
		List<String> result = new ArrayList<String>();
		for(Status status: this.current_status) {
			result.add(status.getUser().getScreenName());
		}
		return result;
	}
	
	/**
	 * Returns a string of all the tweets.
	 */
	public List<String> getStoredStrings(){
		List<String> result = new ArrayList<String>();
		for( Status status: this.current_status ) {
			result.add(status.getText());
		}
		return result;
	}
	
	/**
	 * Returns a list of stored statuses. Statuses store a lot of info which will need to be
	 * retracted.
	 */
	public List<Status> getStoredStatuses(){
		return this.current_status;
	}
}
