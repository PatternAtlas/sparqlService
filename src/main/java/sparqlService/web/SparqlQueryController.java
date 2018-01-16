package sparqlService.web;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import sparqlService.models.SolutionDTO;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class SparqlQueryController {

	@PostMapping(value="/executeQuery")
	public ResponseEntity<?> executeSparqlQuery() {
		String queryIn = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
				"SELECT ?name ?knows\n" +
				"WHERE {\n" +
				"  SERVICE <https://patternpedia.github.io/rdf-playground/rdfa.html> {\n" +
				"    ?s foaf:name ?name ." +
				"    ?s foaf:knows ?knows .  }\n" +
				"}";
		Model model = ModelFactory.createDefaultModel();
		Query query = QueryFactory.create(queryIn);

		QueryExecution qe = QueryExecutionFactory.create(query, model);
		Iterator<QuerySolution> results = qe.execSelect();
		Set<Map<String, String>> result = new HashSet<Map<String, String>>();
		for ( ; results.hasNext() ; )
		{
			QuerySolution soln = results.next();
			Map<String, String> resultVariables = new HashMap<String, String>();
			Iterator<String> varNames = soln.varNames();

			for ( ; varNames.hasNext() ; ) {
				String varName = varNames.next();
				resultVariables.put(varName, soln.get(varName).toString());
				System.out.print("varName: " + varName + " resultVariable: " + soln.get(varName).toString());
			}
			result.add(resultVariables);
		}

		System.out.print(result.size());
		// Free up resources used running the query
		qe.close();

		return new ResponseEntity<Set<Map<String, String>>>(result, HttpStatus.OK);
	}
}
