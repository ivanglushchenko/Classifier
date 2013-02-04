package newsreader.classifier;

import java.io.*;
import java.util.*;

import javax.servlet.http.*;

import newsreader.*;

import com.google.appengine.api.datastore.*;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class ClassifierServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("doGet() is empty");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		
		BagOfLinks bag = getLinks(request);
		if (bag == null) {
			resp.getWriter().println("no arguments were provided");
			return;
		}
		if (bag.UserID == null) {
			resp.getWriter().println("no userID was provided");
			return;
		}
		
		Entity entry = getEntry(bag.UserID);
		if (bag.Links != null && bag.Links.size() > 0) {
			TextSet set = new TextSet();
			for (NewsLink newsLink : bag.Links) {
				List<NewsItem> items = new FeedGetter().getItems(newsLink.Url);
				if (newsLink.IsLike){
					set.setItems(items, ClassificationStatus.Like);
				} else {
					set.setItems(items, ClassificationStatus.Dislike);
				}
			}
			set.train();
			entry.setProperty("model", set.save());
			
			resp.getWriter().println("OK");
		} else if (bag.SampleText != null) {
			//byte[] serializedModel = (byte[])entry.getProperty("model");
			
			//ClassificationStatus result = set.classify(bag.SampleText);
			
			resp.getWriter().println("Hello, world 445");
		}
	}
	
	private BagOfLinks getLinks(HttpServletRequest request){
		StringBuffer jb = new StringBuffer();
		String       line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Gson gson = new Gson();
		return gson.fromJson(jb.toString(), BagOfLinks.class);
	}
	
	private Entity getEntry(String userID) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key key = KeyFactory.createKey("Classifier", userID);
		
		Query query = new Query("Classifier", key);
	    List<Entity> csf = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(5));
	    if (csf.size() == 0) {
	        Date date = new Date();
	        Entity userEntry = new Entity("Classifier", key);
	        userEntry.setProperty("user", userID);
	        userEntry.setProperty("date", date);
	        datastore.put(userEntry);		

	        return getEntry(userID);
	    }
	    return csf.get(0);
	}
}
