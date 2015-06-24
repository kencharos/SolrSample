package jp.co.solr.sample.controller;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jp.co.solr.sample.bean.Criteria;
import jp.co.solr.sample.bean.SearchResults;
import jp.co.solr.sample.service.SearchService;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class SolrController {

	@Autowired
	private SolrClient client;
	
	@Autowired
	private SearchService service;
	
	
	@RequestMapping(value="/hello", method=RequestMethod.GET)
    public @ResponseBody Object sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) throws SolrServerException {
        
		SolrQuery q = new SolrQuery("text:" + name);
		// 
		QueryResponse res = client.query(q);
		
		return res.getResults();
    }
	
	@RequestMapping(value="/removeIndex", method=RequestMethod.GET)
    public @ResponseBody Map<String, String> removeIndex()  {
        
		Map<String, String> map = new HashMap<>();
		
		if(service.removeAll()) {

			map.put("message", "ok:index removed");
		} else {

			map.put("message", "NG");
		}
		return map;
    }
	@RequestMapping(value="/reindex", method=RequestMethod.GET)
    public @ResponseBody Map<String, String> reindex(){

		Map<String, String> map = new HashMap<>();
		
		if(service.reindex()) {

			map.put("message", "ok:reindex Book and Author");
		} else {

			map.put("message", "NG");
		}
		
		return map;
    }

	@RequestMapping(value="/search/suggest", method=RequestMethod.GET)
    public @ResponseBody List<String> suggest(@RequestParam(value="word") String word){

		SolrQuery query = new SolrQuery();
		query.setRequestHandler("/autocomplete_ja");
		query.add("q", word);
		
		QueryResponse resp;
		try {
			resp = client.query(query);
		} catch (SolrServerException e) {
			throw new RuntimeException(e);
		}
	
		if (resp.getSpellCheckResponse() == null || resp.getSpellCheckResponse().getSuggestions() == null) {
			return Collections.emptyList();
		}
		
		List<String> words =  resp.getSpellCheckResponse().getSuggestions()
			.stream().flatMap(s -> {
				String token = s.getToken();
				return resp.getSpellCheckResponse().getSuggestion(token)
						.getAlternatives().stream();
			}).collect(Collectors.toList());
		
		if (!words.isEmpty() || !word.isEmpty()) {
			// wordが登録済みの単語の場合、候補が取得されないので、その場合はwordでも探す。
			Criteria c = new Criteria();
			words.stream().forEach(n -> c.addName(n));
			if (words.isEmpty()) {
				c.addName(word);
			}
			SearchResults res = service.search(c);
			
			List<String> hits = res.result.stream().map(r -> r.getName())
					.collect(Collectors.toList());
			hits.addAll(words);
			words = hits;
		} 
		return words;
    }
	
	
	
	
}
