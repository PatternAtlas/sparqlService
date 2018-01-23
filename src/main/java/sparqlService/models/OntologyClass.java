package sparqlService.models;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class OntologyClass implements Serializable {
	String uri;
	String label;
	String comment;
	Set<DatatypeProperty> dataTypeProperties;
	Set<ObjectProperty> objectProperties;
	Set <String> subClassOf;

	public OntologyClass(String uri, String label, String comment) {
		this.uri = uri;
		this.label = label;
		this.comment = comment;
		this.dataTypeProperties = new HashSet<DatatypeProperty>();
		this.objectProperties = new HashSet<ObjectProperty>();
		this.subClassOf = new HashSet<String>();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Set<DatatypeProperty> getDataTypeProperties() {
		return dataTypeProperties;
	}

	public void setDataTypeProperties(Set<DatatypeProperty> dataTypeProperties) {
		this.dataTypeProperties = dataTypeProperties;
	}

	public Set<ObjectProperty> getObjectProperties() {
		return objectProperties;
	}

	public void setObjectProperties(Set<ObjectProperty> objectProperties) {
		this.objectProperties = objectProperties;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Set<String> getSubClassOf() {
		return subClassOf;
	}

	public void setSubClassOf(Set<String> subClassOf) {
		this.subClassOf = subClassOf;
	}
}
