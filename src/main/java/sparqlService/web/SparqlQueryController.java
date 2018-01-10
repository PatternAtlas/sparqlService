package sparqlService.web;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class SparqlQueryController {

	@PostMapping(value="/executeQuery")
	public ResponseEntity<?> executeSparqlQuery(@RequestBody final String queryIn) {
		Model model = ModelFactory.createDefaultModel();
		Query query = QueryFactory.create(queryIn);

		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();

		ResultSetFormatter.out(System.out, results, query);
		String resultAsText = ResultSetFormatter.asText(results);

		System.out.print(resultAsText);
		// Free up resources used running the query
		qe.close();

		return new ResponseEntity<String>(resultAsText, HttpStatus.OK);
	}
}
