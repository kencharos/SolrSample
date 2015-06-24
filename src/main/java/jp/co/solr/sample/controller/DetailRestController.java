package jp.co.solr.sample.controller;

import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.model.Book;
import jp.co.solr.sample.repository.AuthorRepository;
import jp.co.solr.sample.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class DetailRestController {

	@Autowired
	private BookRepository brepo;
	
	@Autowired
	private AuthorRepository arepo;
	
	@RequestMapping(value="/detail/book/{id}", method=RequestMethod.GET)
    public @ResponseBody Book getBook(@PathVariable String id){
        
		
		return brepo.findOne(Long.parseLong(id));
    }
	
	@RequestMapping(value="/detail/author/{id}", method=RequestMethod.GET)
    public @ResponseBody Author getAuhtor(@PathVariable String id){
        
		
		return arepo.findOne(Long.parseLong(id));
    }
}
