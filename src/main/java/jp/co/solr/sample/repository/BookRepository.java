package jp.co.solr.sample.repository;

import jp.co.solr.sample.model.Book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{

	public Book findByName(String name);
	
}
