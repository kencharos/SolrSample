package jp.co.solr.sample.service;

import java.util.List;

import jp.co.solr.sample.bean.BookBean;
import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.model.Book;
import jp.co.solr.sample.repository.AuthorRepository;
import jp.co.solr.sample.repository.BookRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookService {

	@Autowired
	private AuthorRepository repo;
	@Autowired
	private BookRepository brepo;
	@Autowired
	private SearchService service;
	
	public List<Author> all() {
		return repo.findAll();
	}
	
	@Transactional
	public void regist(BookBean book_) {
		
		Book book = brepo.findByName(book_.getName());
		if(book == null) {
			book = new Book();
		}
		
		book.setName(book_.getName());
		book.setKana(book_.getKana());
		book.setPrice(book_.getPrice());
		book.setIntro(book_.getIntro());
		book.setCategory(book_.getCategory());
		book.setAuthor(repo.getOne(book_.getAuthor()));
		
		if (!brepo.exists(book.getId())) {
			brepo.saveAndFlush(book);
		}
		service.indexBook(book);
		service.commit();
	}
	
}
