package jp.co.solr.sample.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import jp.co.solr.sample.bean.Criteria;
import jp.co.solr.sample.bean.Facet;
import jp.co.solr.sample.bean.Result;
import jp.co.solr.sample.bean.SearchResults;
import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.model.Book;
import jp.co.solr.sample.repository.AuthorRepository;
import jp.co.solr.sample.repository.BookRepository;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CoreAdminParams.CoreAdminAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class SolrSearchService implements SearchService {

	@Autowired
	private SolrClient client;
	
	@Autowired
	private BookRepository book;
	
	@Autowired
	private AuthorRepository author;
	
	@Override
	public boolean removeAll() {
		try {
			client.deleteByQuery("*:*");
			commit();
			return true;
		} catch (SolrServerException | IOException e) {
			return false;
		}
	}
	
	@Override
	public boolean reindex() {
		
		for(Author a : author.findAll()) {
			indexAuthor(a);
		}
		for(Book b : book.findAll()) {
			indexBook(b);
		}

		commit();
		return true;
	}
	
	@Override
	public void indexBook(Book book) {
		
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "book/"+ book.getId());
		doc.addField("name", book.getName());
		doc.addField("contents", book.getIntro());
		doc.addField("kana", book.getKana());
		doc.addField("author", book.getAuthor().getName());
		doc.addField("price_i", book.getPrice());
		doc.addField("category", "書籍");
		doc.addField("sub_category", book.getCategory());
		try {
			client.add(doc);
		} catch (SolrServerException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void indexAuthor(Author author) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", "author/"+author.getId());
		doc.addField("name", author.getName());
		doc.addField("contents", author.getIntro());
		doc.addField("kana", author.getKana());
		doc.addField("author", author.getName());
		doc.addField("category", "著者");
		//doc.addField("sub_category", null);
		try {
			client.add(doc);
		} catch (SolrServerException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void commit() {
		try {
			client.commit(true, true, true);
		} catch (SolrServerException | IOException e) {
			throw new RuntimeException(e);
		}	
	}
	
	@Override
	public String maybe(String str) {

		SolrQuery q = new SolrQuery();
		q.setRequestHandler("/didyoumean_ja");
		q.add("q", str);
		
		QueryResponse resp = null;
		try {
			resp = client.query(q);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
		StringBuilder sb = new StringBuilder();
		System.out.println(resp);
		if (resp.getSpellCheckResponse() != null){
			for(Suggestion sug :resp.getSpellCheckResponse().getSuggestions()) {
				sb.append("," + String.join(",", sug.getAlternatives()));
			}
		}
		return sb.length() > 0 ?  sb.toString().substring(1) : "";
	};
	
	@Override
	public SearchResults search(Criteria criteria) {
		
		//query
		List<String> qs = new ArrayList<>();
		if (inputed(criteria.getText())) {
			String query = String.format("(name:(%s)^3 OR contents:(%s)^1.5 OR author:(%s)^2", 
					criteria.getText(),criteria.getText(),criteria.getText());

			if (criteria.isContainKana()) {
				query += " OR kana:("+criteria.getText()+")^2";
			}
			qs.add(query + ")");
		}
		
		// 通常のカテゴリ検索
		if (inputed(criteria.getCategory())) {
			qs.add("category:"+ criteria.getCategory());
		}
		if(inputed(criteria.getSubCategory())) {
			qs.add("sub_category:"+criteria.getSubCategory());
		}
		if (inputed(criteria.getAuthor())) {
			qs.add("author:"+criteria.getAuthor());
		}
		if (criteria.getPriceFrom() != null && criteria.getPriceTo() != null) {
			qs.add("price_i:[" + criteria.getPriceFrom() + " TO " + criteria.getPriceTo() + "]");
		}
		if (!criteria.names().isEmpty()) {
			
			qs.add("name:" + String.join(" OR ", criteria.names()));
			
		}

		SolrQuery q = new SolrQuery(String.join(" AND ", qs));
		if (!criteria.names().isEmpty()) {
			q.setRows(5);
		}else {
			q.setRows(100);
		}
		// facet
		q.setFacet(true);
		q.addFacetPivotField("category,sub_category");
		// fq
		if (inputed(criteria.getFc())) {
			// ファセットチェックボックス検索
			String cond = Arrays.stream(criteria.getFc().split("_"))
			.map(s -> {
				String[] ss = s.split(",");
				String cat = ss[0];
				List<String> subList = Arrays.asList(ss).subList(1, ss.length);
				String sub = subList.stream()
					.map(a -> "sub_category:" + a)
					.collect(Collectors.joining(" OR "));
				return "(category:" + cat +
						(subList.isEmpty() ? "" :  " AND "+ 
								( subList.size() > 1 ? "(" + sub + ")" : sub))
					+")"; 
			}).collect(Collectors.joining(" OR "));
			q.add("fq", cond);
		} 
		
		// highlight
		if (criteria.useHighlight()) {

			q.setHighlight(true);
			q.setHighlightSimplePre("<em>");
			q.setHighlightSimplePost("</em>");
			q.addHighlightField("name,contents");
		}
		
		
		QueryResponse res = null;
		try {
			System.out.println(q.toString());
			res = client.query(q);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
		
		
		// hl
		Map<String, Map<String, List<String>>> hlmap =  res.getHighlighting();
		BiFunction<String, String, Optional<String>> hlget = (key, name) -> {
			if (hlmap != null && hlmap.containsKey(key) && hlmap.get(key).containsKey(name)) {
				return Optional.of(hlmap.get(key).get(name).get(0));
			} else {
				return Optional.empty();
			}
		};
		
		// result
		List<Result> result = res.getResults().stream().map(sd ->{
			Result r = new Result();
			String id = sd.getFieldValue("id").toString();
			r.setId(id);
			r.setName(hlget.apply(id, "name")
					.orElseGet(() -> sd.getFieldValue("name").toString()));
			r.setContents(hlget.apply(id, "contents")
					.orElseGet(() -> sd.getFieldValue("contents").toString()));
			r.setCategory(sd.getFieldValue("category").toString());
			if (sd.getFieldValue("sub_category") != null) {
				r.setSubCategory(sd.getFieldValue("sub_category").toString());
			}
			
			if (id.startsWith("book")) {
				r.setAuthor(sd.getFieldValue("author").toString());
			}
			
			return r;
		}).collect(Collectors.toList());
		
		List<Facet> facet = res.getFacetPivot().get("category,sub_category").stream().map(pv -> {
			Facet fc = new Facet(pv.getValue().toString(), pv.getCount());
			if (pv.getPivot() != null) {

				pv.getPivot().forEach(sub -> {
					fc.addChild(sub.getValue().toString(), sub.getCount());
				});
			}
			return fc;
		}).collect(Collectors.toList());
		String dym = "";
		if (inputed(criteria.getText())) {
			dym = this.maybe(criteria.getText());
		}
	
		return new SearchResults(facet, result,dym);
	}
	
	private boolean inputed(String str){
		return str != null && str.length() > 0;
		
	}
	
	public void reload(){
		
		CoreAdminRequest req = new CoreAdminRequest();
		req.setAction(CoreAdminAction.RELOAD);
		//req.process(client);
		
	}
	
}
