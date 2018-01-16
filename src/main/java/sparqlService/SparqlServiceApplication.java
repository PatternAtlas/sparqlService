package sparqlService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;



@ComponentScan(basePackages = "sparqlService")
@EnableAutoConfiguration
public class SparqlServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SparqlServiceApplication.class, args);
	}

}
