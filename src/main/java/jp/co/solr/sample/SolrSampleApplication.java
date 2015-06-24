package jp.co.solr.sample;

import java.util.Arrays;

import jp.co.solr.sample.model.Author;
import jp.co.solr.sample.model.Book;
import jp.co.solr.sample.repository.AuthorRepository;
import jp.co.solr.sample.repository.BookRepository;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.solr.SolrAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration.WebMvcAutoConfigurationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@SpringBootApplication
@EnableWebMvc
@EnableAutoConfiguration(exclude=SolrAutoConfiguration.class)
@Configuration
public class SolrSampleApplication extends WebMvcAutoConfigurationAdapter implements CommandLineRunner{

    public static void main(String[] args) {
    	Arrays.stream(args).forEach(System.out::println);
        SpringApplication.run(SolrSampleApplication.class, args);
    }
    
    @Autowired
    private AuthorRepository repo;
    @Autowired
    private BookRepository brepo;
    
    @Override
    @Transactional
    public void run(String... arg0) throws Exception {
    	// add sample data
    	Author a = new Author();
    	a.setName("田中三郎");
    	a.setKana("タナカサブロウ");
    	a.setIntro("新進気鋭の新人");
    	
    	if (repo.findByName(a.getName()) == null) {
    		repo.save(a);
    	}
    	Book b = new Book();
    	b.setAuthor(repo.findByName(a.getName()));
    	b.setCategory("紙");
    	b.setName("人生の手引き");
    	b.setKana("ジンセイノテビキ");
    	b.setIntro("メディアで騒然の話題作");
    	b.setPrice(580);
    	
    	if (brepo.findByName(b.getName()) == null) {
    		brepo.save(b);
    	}
    }
    
    @Bean
    public SolrClient getConfiguredSolrClient(){
    	//TODO Spring Solr を使うのもあり。
    	SolrClient client = new HttpSolrClient("http://localhost:8983/solr/books");
    	return client;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    	super.addResourceHandlers(registry);
    	registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    	
    }
}
