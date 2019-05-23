
package uk.ac.ebi.threei;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//need this annotation if running the rest web service
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan({"uk.ac.ebi.threei.rest", "uk.ac.ebi.threei.rest.repositories"})
@SpringBootApplication
public class Application {

	@NotNull
	@Value("${solr.host}")
	//@Value("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr")
	private String solrBaseUrl;
	
	@Bean(name = "solrClient")
	HttpSolrClient gtSolrClient() {
		return new HttpSolrClient.Builder(solrBaseUrl+ "/gene").build();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
