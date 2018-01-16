package sparqlService.web;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class SparqlQueryController {

	@PostMapping(value="/executeQuery")
	public ResponseEntity<?> executeSparqlQuery(@RequestBody String queryIn) {
//		String queryIn = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
//				"SELECT ?name ?knows\n" +
//				"WHERE {\n" +
//				"  SERVICE <https://patternpedia.github.io/rdf-playground/rdfa.html> {\n" +
//				"    ?s foaf:name ?name ." +
//				"    ?s foaf:knows ?knows .  }\n" +
//				"}";
		Model model = ModelFactory.createDefaultModel();
		Query query = QueryFactory.create(queryIn);

		QueryExecution qe = QueryExecutionFactory.create(query, model);
		Iterator<QuerySolution> querySolutions = qe.execSelect();

		Set<Map<String, String>> result = new HashSet<Map<String, String>>();
		for ( ; querySolutions.hasNext() ; )
		{
			Map<String, String> resultVariablesOfSolution = new HashMap<String, String>();
			QuerySolution soln = querySolutions.next();
			Iterator<String> varNames = soln.varNames();

			for ( ; varNames.hasNext() ; ) {
				String varName = varNames.next();
				resultVariablesOfSolution.put(varName, soln.get(varName).toString());
				System.out.print("varName: " + varName + " resultVariable: " + soln.get(varName).toString());
			}
			result.add(resultVariablesOfSolution);
		}

		// Free up resources used running the query
		qe.close();

		return new ResponseEntity<Set<Map<String, String>>>(result, HttpStatus.OK);
	}
}
