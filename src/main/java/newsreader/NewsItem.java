package newsreader;

public class NewsItem {
	public NewsItem(String url, String content, ClassificationStatus classificationStatus){
		_url = url;
		_content = content;
		_classificationStatus = classificationStatus;
	}
	
	private String _url;
	public String getUrl() {
		return _url;
	}
	
	private String _content;
	public String getContent() {
		return _content;
	}
	
	private ClassificationStatus _classificationStatus;
	public ClassificationStatus getClassificationStatus(){
		return _classificationStatus;
	}
}