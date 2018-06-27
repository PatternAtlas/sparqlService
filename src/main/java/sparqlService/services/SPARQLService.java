package sparqlService.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.springframework.stereotype.Service;

@Service
public class SPARQLService {

    public SPARQLService() {}

    public Set<Map<String, String>> executeQuery(String queryAsString) {
        Model model = ModelFactory.createDefaultModel();
        Query query = QueryFactory.create(queryAsString);

        QueryExecution qe = QueryExecutionFactory.create(query, model);
        Iterator<QuerySolution> querySolutions = qe.execSelect();
        Set<Map<String, String>> result = new HashSet<Map<String, String>>();
        for (; querySolutions.hasNext(); ) {
            Map<String, String> resultVariablesOfSolution = new HashMap<String, String>();
            QuerySolution soln = querySolutions.next();
            Iterator<String> varNames = soln.varNames();

            for (; varNames.hasNext(); ) {
                String varName = varNames.next();
                resultVariablesOfSolution.put(varName, soln.get(varName).toString());
                System.out.print("varName: " + varName + " resultVariable: " + soln.get(varName).toString());
            }
            result.add(resultVariablesOfSolution);
        }

        // Free up resources used running the query
        qe.close();

        return result;
    }
}
