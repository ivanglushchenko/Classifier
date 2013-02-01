package newsreader.classifier;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;

public class ClassifierTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException, IOException {
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
		
		BagOfLinks bag = new BagOfLinks();
		bag.UserID = "User 111";
		bag.Links = list;
		return bag;
	}


}
