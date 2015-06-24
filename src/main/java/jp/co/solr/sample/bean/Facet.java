package jp.co.solr.sample.bean;

import java.util.ArrayList;
import java.util.List;

public class Facet {
	private final String name;
	private final int count;
	
	private List<SubFacet> children = new ArrayList<>();
	
	public Facet(String name, int count) {
		this.name = name;
		this.count = count;
	}

	public void addChild(String name, int count) {
		children.add(new SubFacet(this, name, count));
	}
	
	public String getQuery() {
		return "category=" + name;
	}
	
	public List<SubFacet> getChildren(){
		return this.children;
	}
	
	public boolean hasChild(){
		return !children.isEmpty();
	}
	
	public String getName() {
		return name;
	}
	
	public int getCount(){
		return this.count;
	}
	
	public static class SubFacet {
		
		private final Facet parent;
		
		private final String name;
		
		private final int count;
		
		public SubFacet(Facet parent, String name, int count){
			this.parent = parent;
			this.name = name;
			this.count = count;
		}

		public String getQuery() {
			return "category=" + parent.name + "&subCategory=" + name;
		}
		
		public String getName() {
			return name;
		}

		public int getCount() {
			return count;
		}
		
		
		
	}

}
