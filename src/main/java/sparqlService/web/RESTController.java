package sparqlService.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.hp.hpl.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sparqlService.models.OntologyClass;
import sparqlService.models.RdfGraph;
import sparqlService.services.CrawlerService;
import sparqlService.services.SPARQLService;
import sparqlService.services.VocabularyService;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class RESTController {

    @Autowired
    private SPARQLService sparqlService;

    @Autowired
    private CrawlerService crawlerService;

    @Autowired
    private VocabularyService vocabularyService;

    @PostMapping(value = "/executeQuery")
    public ResponseEntity<?> executeSparqlQuery(@RequestBody String queryIn) {
        Set<Map<String, String>> result = sparqlService.executeQuery(queryIn);
        return new ResponseEntity<Set<Map<String, String>>>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/crawlPattern")
    public ResponseEntity<?> readFile(@RequestBody List<String> patternUrls) throws IOException {
        Model model = crawlerService.crawlPatternGraph(patternUrls);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        model.write(os, "TURTLE");
        return new ResponseEntity<RdfGraph>(new RdfGraph(os.toString()), HttpStatus.OK);
    }

    @PostMapping(value = "/getClasses")
    public ResponseEntity<?> getClassesOfOntology(@RequestBody String uri) {
        Set<OntologyClass> classesOfOntology = vocabularyService.getClasses(uri);
        return new ResponseEntity<Set<OntologyClass>>(classesOfOntology, HttpStatus.OK);
    }

}
