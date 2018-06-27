package sparqlService.services;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.springframework.stereotype.Service;

@Service
public class CrawlerService {
    public CrawlerService() {
    }

    public Model crawlPatternGraph(List<String> patternUrls) {
        List<String> readUrls = new ArrayList<String>();
        Model model = ModelFactory.createOntologyModel();
        for (String url : patternUrls) {
            model.read(url);
            readUrls.add(url);
        }
        model = crawlExternalPatterns(model, readUrls);

        return model;
    }

    private Model crawlExternalPatterns(Model model, List<String> readUrls) {
        OntModel vocab = ModelFactory.createOntologyModel();
        vocab.read("https://patternpedia.github.io/linkedOpenPatternClient/assets/vocabulary/semantic-pattern.rdf");
        ExtendedIterator<? extends OntProperty> objectPropertyIter = vocab.listObjectProperties();
        while (objectPropertyIter.hasNext()) {
            OntProperty property = objectPropertyIter.next();

            NodeIterator resourceIter = model.listObjectsOfProperty(property);
            while (resourceIter.hasNext()) {
                Resource object = (Resource) resourceIter.next();
                if (!readUrls.contains(object.getURI())) {
                    model.read(object.getURI());
                    readUrls.add(object.getURI());
                }
            }
        }
        return model;
    }
}
