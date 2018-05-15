package sparqlService.web;

import com.github.jsonldjava.utils.Obj;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sparqlService.models.DatatypeProperty;
import sparqlService.models.ObjectProperty;
import sparqlService.models.OntologyClass;
import sparqlService.models.RdfGraph;


@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class SparqlQueryController {

	@PostMapping(value = "/executeQuery")
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

		return new ResponseEntity<Set<Map<String, String>>>(result, HttpStatus.OK);
	}

	@PostMapping(value = "/getClasses")
	public ResponseEntity<?> getClassesOfOntology(@RequestBody String uri) {
		OntModel model = ModelFactory.createOntologyModel();
		model.read(uri);
		Set<OntologyClass> classesOfOntology = new HashSet<OntologyClass>();
		ExtendedIterator classes = model.listClasses();
		while (classes.hasNext()) {
			OntClass thisClass = (OntClass) classes.next();
			OntologyClass ontClass = new OntologyClass(thisClass.getURI(), thisClass.getLocalName(), thisClass.getComment(null));
			ontClass.setDataTypeProperties(getDatatypeProperties(thisClass));
			ontClass.setObjectProperties(getObjectProperties(thisClass));
			ontClass.setSubClassOf(getUriOfSuperClasses(thisClass));
			classesOfOntology.add(ontClass);
		}
		return new ResponseEntity<Set<OntologyClass>>(classesOfOntology, HttpStatus.OK);
	}


	@PostMapping(value = "/crawlPattern")
	public ResponseEntity<?> readFile(@RequestBody List<String> patternUrls) throws IOException {
		List<String> readUrls = new ArrayList<String>();
		Model model = ModelFactory.createOntologyModel();
		for(String url : patternUrls){
			model.read(url);
			readUrls.add(url);
		}
		model = crawlExternalPatterns(model, readUrls);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		model.write(os, "TURTLE");

		return new ResponseEntity<RdfGraph>(new RdfGraph(os.toString()), HttpStatus.OK);
	}

	private Model crawlExternalPatterns(Model model, List<String> readUrls){
		OntModel vocab = ModelFactory.createOntologyModel();
		vocab.read("https://patternpedia.github.io/linkedOpenPatternClient/assets/vocabulary/semantic-pattern.rdf");
		ExtendedIterator<? extends OntProperty> objectPropertyIter = vocab.listObjectProperties();
		while(objectPropertyIter.hasNext()){
			OntProperty property = objectPropertyIter.next();

			NodeIterator resourceIter = model.listObjectsOfProperty(property);
			while (resourceIter.hasNext())
			{
				Resource object =  (Resource) resourceIter.next();
				if(!readUrls.contains(object.getURI())){
					model.read(object.getURI());
					readUrls.add(object.getURI());
				}

			}
		}
		return model;
	}

	private Set<OntologyClass> getVocabulary() {
		OntModel model = ModelFactory.createOntologyModel();
		model.read("https://patternpedia.github.io/linkedOpenPatternClient/assets/vocabulary/semantic-pattern.rdf");
		Set<OntologyClass> classesOfOntology = new HashSet<OntologyClass>();
		ExtendedIterator classes = model.listClasses();
		while (classes.hasNext()) {
			OntClass thisClass = (OntClass) classes.next();
			OntologyClass ontClass = new OntologyClass(thisClass.getURI(), thisClass.getLocalName(), thisClass.getComment(null));
			ontClass.setDataTypeProperties(getDatatypeProperties(thisClass));
			ontClass.setObjectProperties(getObjectProperties(thisClass));
			ontClass.setSubClassOf(getUriOfSuperClasses(thisClass));
			classesOfOntology.add(ontClass);
		}
		return classesOfOntology;
	}


	private Set<DatatypeProperty> getDatatypeProperties(OntClass ontologyClass) {
		ExtendedIterator<? extends OntProperty> properties = ontologyClass.listDeclaredProperties(true);
		Set<DatatypeProperty> datatypeProperties = new HashSet<DatatypeProperty>();
		while (properties.hasNext()) {
			OntProperty property = properties.next();
			if (property.isDatatypeProperty()) {
				Set<String> range = getRange(property);
				datatypeProperties.add(new DatatypeProperty(property.getURI(), property.getLocalName(), range));
			}
		}
		return datatypeProperties;
	}

	private Set<ObjectProperty> getObjectProperties(OntClass ontologyClass) {
		ExtendedIterator<? extends OntProperty> properties = ontologyClass.listDeclaredProperties(true);
		Set<ObjectProperty> objectProperties = new HashSet<ObjectProperty>();
		while (properties.hasNext()) {
			OntProperty property = properties.next();
			if (property.isObjectProperty()) {
				Set<String> range = getRange(property);
				objectProperties.add(new ObjectProperty(property.getURI(), property.getLocalName(), range));
			}
		}
		return objectProperties;
	}

	private Set<String> getRange(OntProperty property) {
		ExtendedIterator<? extends OntResource> ranges = property.listRange();
		HashSet<String> propertyRange = new HashSet<String>();
		while (ranges.hasNext()) {
			propertyRange.add(ranges.next().toString());
		}
		return propertyRange;
	}

	private Set<String> getUriOfSuperClasses(OntClass ontClass) {
		ExtendedIterator<? extends OntClass> superClasses = ontClass.listSuperClasses(true);
		Set<String> uriOfSuperClasses = new HashSet<String>();
		while (superClasses.hasNext()) {
			uriOfSuperClasses.add(superClasses.next().getURI());
		}
		return uriOfSuperClasses;
	}

}
