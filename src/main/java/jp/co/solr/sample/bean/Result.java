package jp.co.solr.sample.bean;

public class Result {

	private String id;
	
	private String name;

	private String author;
	
	private String contents;
	
	private String category;
	
	private String subCategory;
	
	public Result(){}

	public Result(String id, String name, String contents, String category,
			String subCategory,String author) {
		super();
		this.id = id;
		this.name = name;
		this.contents = contents;
		this.category = category;
		this.subCategory = subCategory;
		this.author = author;
	}

	public String getFmtName() {
		return name + (author == null ? "" : "(" + author + ")");
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
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

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	
	
	
}
