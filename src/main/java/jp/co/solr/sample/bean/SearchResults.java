package jp.co.solr.sample.bean;

import java.util.List;

public class SearchResults {

	public final List<Facet> facet;
	
	public final List<Result> result;
	
	public final String didYouMean;

	public SearchResults(List<Facet> facet, List<Result> result, String didYouMean) {
		super();
		this.facet = facet;
		this.result = result;
		this.didYouMean = didYouMean;
	}
	
	
	
	
}
