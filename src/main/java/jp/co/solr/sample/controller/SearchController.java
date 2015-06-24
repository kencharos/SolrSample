package jp.co.solr.sample.controller;

import javax.validation.Valid;

import jp.co.solr.sample.bean.Criteria;
import jp.co.solr.sample.bean.SearchResults;
import jp.co.solr.sample.service.SearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class SearchController {

	@Autowired
	private SearchService service;
	
	@RequestMapping(value= "/search", method = RequestMethod.GET)
	public String search(@Valid Criteria criteria, BindingResult res ,Model model) {
		
		if (criteria == null || criteria.isInit()) {
			return "search";
		}
		
		criteria.doHighlight();
		SearchResults results = service.search(criteria);
		
		model.addAttribute("facet", results.facet);
		model.addAttribute("list", results.result);
		model.addAttribute("dym", results.didYouMean);
		model.addAttribute("query", criteria.queryString());
		
		return "search";
		
    }
}
