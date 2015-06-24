package jp.co.solr.sample.controller;


import javax.validation.Valid;

import jp.co.solr.sample.bean.BookBean;
import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.service.AuthorService;
import jp.co.solr.sample.service.BookService;
import jp.co.solr.sample.service.SearchService;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ApplicationController {

	@Autowired
	private AuthorService authorService;
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private SearchService searchService;
	
	@RequestMapping("/")
	public String sayHello() {
		return "index";
    }

	// バリデーションする場合、GETの時点でbeanを引数にして、formのth:objectに書いておく。
	@RequestMapping(value= "/book", method = RequestMethod.GET)
	public String initialBook(BookBean bookBean, Model model) {
		model.addAttribute("authors", authorService.all());
		return "book";
    }
	// その場合だけ、@Validが有効になる。
	@RequestMapping(value= "/book", method = RequestMethod.POST)
	public String initialBook(@Valid BookBean bookBean, BindingResult res, Model model) {
		
		if (res.hasErrors()) {
			System.out.println("validation error");
			model.addAttribute("authors", authorService.all());
			return "book";
		}
		bookService.regist(bookBean);
		return "redirect:/book";
    }
	
	@RequestMapping(value= "/author", method = RequestMethod.GET)
	public String initialAuthor(Author author, Model model) {
		model.addAttribute("authors", authorService.all());
	
		return "author";
    }
	// その場合だけ、@Validが有効になる。
	@RequestMapping(value= "/author", method = RequestMethod.POST)
	public String initialBook(@Valid Author author, BindingResult res) {
		
		if (res.hasErrors()) {
			return "author";
		}
		authorService.register(author);
		return "redirect:/author";
    }
	
	
}
