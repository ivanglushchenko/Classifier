package newsreader.classifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import newsreader.*;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.misc.SerializedClassifier;
import weka.core.*;
import weka.core.stemmers.IteratedLovinsStemmer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class TextSet {
	private FastVector _attributes;
	private Attribute _attributeClass;
	private Attribute _attributeText;
	private Instances _rawDataSet;
	private Instances _trainingSet;
	private StringToWordVector _filter;
	private Classifier _classifier;
	
	public TextSet(){
		FastVector fvClassVal = new FastVector(3);
		fvClassVal.addElement("neutral");
		fvClassVal.addElement("like");
		fvClassVal.addElement("dislike");
		_attributeClass = new Attribute("theClass", fvClassVal);
		
		_attributeText = new Attribute("text", (FastVector) null);
		
		_attributes = new FastVector(4);
		_attributes.addElement(_attributeClass);
		_attributes.addElement(_attributeText);    

		_rawDataSet = new Instances("Rel", _attributes, 10);           
		_rawDataSet.setClassIndex(0);
		
	    _filter = new StringToWordVector();
	    _filter.setIDFTransform(true);
	    _filter.setUseStoplist(true);
	    _filter.setStemmer(new IteratedLovinsStemmer());
	}
	
	public void setItems(List<NewsItem> items, ClassificationStatus cs){
		for (NewsItem item : items) {
			Instance instance = new Instance(2);
			instance.setValue(_attributeClass, classificationToString(cs));
			instance.setValue(_attributeText, _attributeText.addStringValue(item.getContent()));
			_rawDataSet.add(instance);
		}
	}
	
	public void train() {
	    try {
			_filter.setInputFormat(_rawDataSet);
			_trainingSet = Filter.useFilter(_rawDataSet, _filter);
	    	
			_classifier = new NaiveBayes();
			_classifier.buildClassifier(_trainingSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] save() {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			Object[] args = { _classifier };
			weka.core.SerializationHelper.writeAll(os, args);
			return os.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void load(byte[] clsf) throws Exception {
		_filter.setInputFormat(_rawDataSet);
		_trainingSet = Filter.useFilter(_rawDataSet, _filter);

		ByteArrayInputStream is = new ByteArrayInputStream(clsf);
		Object[] all = weka.core.SerializationHelper.readAll(is);
		NaiveBayes nbc = (NaiveBayes)all[0];
		
		SerializedClassifier classifier = new SerializedClassifier();
		classifier.setModel(nbc);
		_classifier = classifier;
	}
	
	public double[] classifyRaw(String line){
		Instances ds = _rawDataSet.stringFreeStructure();           

		Instance instance = new Instance(2);
		Attribute ta = ds.attribute("text");
		instance.setValue(ta, ta.addStringValue(line));
		instance.setDataset(ds);

        try {
			_filter.input(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
        instance = _filter.output();
        try {
			return _classifier.distributionForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	public ClassificationStatus classify(String line){
		double[] scores = classifyRaw(line);
		if (scores == null || scores.length != 3) return ClassificationStatus.Undefined;
		
		if (scores[0] >= scores[1] && scores[0] >= scores[2]) return ClassificationStatus.Neutral;
		if (scores[1] >= scores[0] && scores[1] >= scores[2]) return ClassificationStatus.Like;
        return ClassificationStatus.Dislike;
	}
	
	private String classificationToString(ClassificationStatus cs) {
		switch (cs){
		case Like:
			return "like";
		case Dislike:
			return "dislike";
		case Neutral:
			return "neutral";
		default:
			break;
		}
		return null;
	}
}