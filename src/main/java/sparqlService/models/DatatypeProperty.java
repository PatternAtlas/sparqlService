package sparqlService.models;

import java.io.Serializable;
import java.util.Set;

public class DatatypeProperty implements Serializable {
	String uri;
	String label;
	Set<String> range;

	public DatatypeProperty(String uri, String label, Set<String> range) {
		this.uri = uri;
		this.label = label;
		this.range = range;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<String> getRange() {
		return range;
	}

	public void setRange(Set<String> range) {
		this.range = range;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

}
