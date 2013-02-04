package newsreader.classifier;

import java.io.*;
import java.net.*;
import java.util.*;

import newsreader.*;

import com.google.gson.Gson;

public class ClassifierTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws Exception {
		//classifyLocally();
		classifyRemotely();
	}
	
	public static void classifyLocally() throws Exception {
		TextSet set = new TextSet();
		List<NewsItem> likes = new FeedGetter().getItems("http://feeds.arstechnica.com/arstechnica/index/");
		List<NewsItem> dislikes = new FeedGetter().getItems("http://www.chow.com/feed/stories-and-recipes");
		set.setItems(likes, ClassificationStatus.Like);
		set.setItems(dislikes, ClassificationStatus.Dislike);
		set.train();

		for (NewsItem ni : likes) {
			System.out.println("t1: " + set.classify(ni.getContent()));
		}
		for (NewsItem ni : dislikes) {
			System.out.println("t2: " + set.classify(ni.getContent()));
		}
		
		byte[] sModel = set.save();
		TextSet newSet = new TextSet();
		newSet.setItems(likes, ClassificationStatus.Like);
		newSet.setItems(dislikes, ClassificationStatus.Dislike);
		newSet.load(sModel);
		
		for (NewsItem ni : likes) {
			System.out.println("s1: " + newSet.classify(ni.getContent()));
		}
		for (NewsItem ni : dislikes) {
			System.out.println("s2: " + newSet.classify(ni.getContent()));
		}
	}
	
	public static void classifyRemotely() throws Exception {
		Gson gson = new Gson();
		String jsonContent = gson.toJson(getTestBagOfLinks());
		
		HttpURLConnection conn = (HttpURLConnection)new URL("http://localhost:8888/classifier").openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(jsonContent.length()));
		OutputStream os = conn.getOutputStream();
		os.write( jsonContent.getBytes() );		
		
		InputStream is = conn.getInputStream();
		String result = convertStreamToString(is);
		
		System.out.println("and the result is: " + result);
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
		Scanner scanner = new Scanner(is);
	    java.util.Scanner s = scanner.useDelimiter("\\A");
	    String ret = s.hasNext() ? s.next() : "";
	    scanner.close();
	    return ret;
	}

	public static BagOfLinks getTestBagOfLinks() {
		ArrayList<NewsLink> list = new ArrayList<NewsLink>();
		/*
		NewsLink link1 = new NewsLink();
		link1.Url = "http://feeds.arstechnica.com/arstechnica/index/";
		link1.IsLike = true;
		link1.IsFeed = true;
		list.add(link1);
		
		NewsLink link2 = new NewsLink();
		link2.Url = "http://www.chow.com/feed/stories-and-recipes";
		link2.IsLike = false;
		link2.IsFeed = true;
		list.add(link2);
		*/
		BagOfLinks bag = new BagOfLinks();
		bag.UserID = "User 111";
		bag.Links = list;
		bag.SampleText = "The examples are everywhere: In November, we reported that malware was used to steal information about one of Japan's newest rockets and upload it to computers controlled by hackers. Critical systems at two US power plants were recently found infected with malware spread by USB drives. Malware known as Dexter stole credit card data from point-of-sale terminals at businesses. And espionage-motivated computer threats are getting more sophisticated and versatile all the time.";
		return bag;
	}
}
