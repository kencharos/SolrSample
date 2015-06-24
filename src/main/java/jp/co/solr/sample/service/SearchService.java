package jp.co.solr.sample.service;

import jp.co.solr.sample.bean.Criteria;
import jp.co.solr.sample.bean.SearchResults;
import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.model.Book;

/** 検索系処理のインターフェース。
 *  Solr と GSAで共通処理を提供することを想定。
 *  */
public interface SearchService {

	/** インデックス全削除 */
	boolean removeAll();
	
	/** 書籍、著者インデックス再作成 */
	boolean reindex();
	
	void indexBook(Book book);
	void indexAuthor(Author author);
	
	void commit();
	
	SearchResults search(Criteria criteria);
	
	String maybe(String str);
}
