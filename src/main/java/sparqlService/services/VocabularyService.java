package sparqlService.services;

import java.util.HashSet;
import java.util.Set;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import sparqlService.models.DatatypeProperty;
import sparqlService.models.ObjectProperty;
import sparqlService.models.OntologyClass;

public class VocabularyService {

    public VocabularyService() {
    }

    public Set<OntologyClass> getClasses(String vocabularyUri){
        OntModel model = ModelFactory.createOntologyModel();
        model.read(vocabularyUri);
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
