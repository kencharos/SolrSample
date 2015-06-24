package jp.co.solr.sample.bean;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BookBean {

	@NotNull
	@Size(min=1, max=100)
	private String name;

	@NotNull
	@Size(min=1, max=100)
	private String kana;
	
	@NotNull
	@Size(min=1, max=300)
	private String intro;

	@NotNull
	@Min(1)
	private Integer price;
	
	@NotNull
	private Long author;

	@NotNull
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKana() {
		return kana;
	}

	public void setKana(String kana) {
		this.kana = kana;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Long getAuthor() {
		return author;
	}

	public void setAuthor(Long author) {
		this.author = author;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}
	
	
}
