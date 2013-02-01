package newsreader.classifier;

import java.io.*;
import javax.servlet.http.*;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class ClassifierServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		resp.setContentType("text/plain");
		resp.getWriter().println("doGet() is empty");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse resp) throws IOException {
		BagOfLinks links = getLinks(request);
		if (links != null) {
			
		}
		
		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world 445");
	}
	
	private BagOfLinks getLinks(HttpServletRequest request){
		StringBuffer jb = new StringBuffer();
		String       line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null)
				jb.append(line);
		} catch (Exception e) { /*report an error*/ }

		Gson gson = new Gson();
		return gson.fromJson(jb.toString(), BagOfLinks.class);
	}
}
