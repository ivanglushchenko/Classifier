package newsreader.classifier;

import java.io.*;
import java.net.*;
import java.util.*;

import org.htmlcleaner.*;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;

@SuppressWarnings("rawtypes")
public class FeedGetter {
	public FeedGetter(){
		
	}
	
	private HtmlCleaner _cleaner = new HtmlCleaner();
	
	public List<NewsItem> getItems(String url) {
		ArrayList<NewsItem> list = new ArrayList<NewsItem>();
		
		try{
			HttpURLConnection conn = null;
			Map<String, String> env = System.getenv();
			if (env.containsKey("Dhttp.proxyHost") && env.containsKey("Dhttp.proxyPort")){
				String host = env.get("Dhttp.proxyHost");
				if (host.startsWith("http://")) {
					host = host.substring(7);
				}
				String postString = env.get("Dhttp.proxyPort");
				Integer port = Integer.parseInt(postString);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
				conn = (HttpURLConnection)new URL(url).openConnection(proxy);
			} else {
				conn = (HttpURLConnection)new URL(url).openConnection();
			}
			InputStream is = conn.getInputStream();
			XmlReader xmlReader = new XmlReader(is);
	        SyndFeedInput input = new SyndFeedInput();
	        SyndFeed feed = input.build(xmlReader);
			List entries = feed.getEntries();
	        for (Iterator iter = entries.iterator(); iter.hasNext(); ) {
	        	SyndEntry entry = (SyndEntry)iter.next();
	        	String content = cleanString(getEntryContent(entry).toString());
	        	if (content.isEmpty()){
	        		SyndContent description = entry.getDescription();
	        		if (description != null){
	        			content = cleanString(description.getValue());
	        		}
	        	}
	        	list.add(new NewsItem(entry.getUri(), content, ClassificationStatus.Like));
	        }
		}
		catch (Exception exc){
			System.out.println(exc.toString());
		}
		return list;
	}
	
	private StringBuffer getEntryContent(SyndEntry entry) {
		StringBuffer sb = new StringBuffer();
		for (Iterator i = entry.getContents().iterator(); i.hasNext();){
			SyndContent content = (SyndContent)i.next();
			sb.append(content.getValue());
		}
		
		return sb;
	}
	
	private String cleanString(String content) {
    	TagNode cleanedNode = _cleaner.clean(content);
    	return cleanedNode.getText().toString()
    			.replace('\n', ' ')
    			.replace('“', ' ')
    			.replace('”', ' ')
    			.replace('–', ' ')
    			.replace('\t', ' ')
    			.replace('?', ' ')
    			.trim();
	}
}
