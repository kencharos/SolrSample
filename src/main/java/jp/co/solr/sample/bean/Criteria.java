package jp.co.solr.sample.bean;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

/** 検索条件 */
public class Criteria {

	private String text;
	
	private String author;
	
	private boolean containKana;
	
	private String category;
	
	private String subCategory;
	
	private String fc;
	
	private List<String> names = new ArrayList<>();
	
	private boolean highlight;
	
	@Min(0)
	private Integer priceFrom;

	@Min(0)
	private Integer priceTo;

	private String type;
	
	public boolean isInit() {
		return type != null && type.equalsIgnoreCase("init");
	}
	
	public String queryString() {
		
		List<String> list = new ArrayList<>();
		if (notEmp(text)) {
			list.add("text="+text);
		}
		if(notEmp(author)) {
			list.add("author="+ author);
		}
		if(isContainKana()) {
			list.add("containKana=" + "on");
		}
		if(priceFrom != null) {
			list.add("priceFron=" + priceFrom);
		}
		if(priceTo != null) {
			list.add("priceTo=" + priceTo);
		}
		return "?" + String.join("&", list) + (list.isEmpty() ? "" : "&");
		
	}
	private boolean notEmp(String s) {
		return s != null && s.length() > 0;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean useHighlight() {
		return this.highlight;
	}
	
	public void doHighlight() {
		this.highlight = true;
	}

	public boolean isContainKana() {
		return containKana;
	}

	public void setContainKana(boolean containKana) {
		this.containKana = containKana;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}

	public Integer getPriceFrom() {
		return priceFrom;
	}

	public void setPriceFrom(Integer priceFrom) {
		this.priceFrom = priceFrom;
	}

	public Integer getPriceTo() {
		return priceTo;
	}

	public void setPriceTo(Integer priceTo) {
		this.priceTo = priceTo;
	}

	public String getFc() {
		return fc;
	}

	public void setFc(String fc) {
		this.fc = fc;
	}

	public void addName(String name) {
		names.add(name);
	}
	
	public List<String> names(){
		return this.names;
	}
}
