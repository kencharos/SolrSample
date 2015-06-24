package jp.co.solr.sample.service;

import java.util.List;

import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.repository.AuthorRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorService {

	@Autowired
	private AuthorRepository repo;
	
	@Autowired
	private SearchService service;
	
	public List<Author> all() {
		return repo.findAll();
	}
	@Transactional
	public void register(Author author) {
		
		Author exists = repo.findByName(author.getName());
		if(exists != null) {
			exists.setName(author.getName());
			exists.setKana(author.getKana());
			repo.flush();
		} else {
			repo.saveAndFlush(author);
			exists = author;
		}
		
		service.indexAuthor(exists);
		service.commit();
		
	}
	
}
