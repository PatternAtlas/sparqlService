package sparqlService.models;

import java.io.Serializable;

public class RdfGraph implements Serializable {
	String graphAsTurtleString;

	public RdfGraph(String graphAsTurtleString) {
		this.graphAsTurtleString = graphAsTurtleString;
	}

	public String getGraphAsTurtleString() {
		return graphAsTurtleString;
	}

	public void setGraphAsTurtleString(String graphAsTurtleString) {
		this.graphAsTurtleString = graphAsTurtleString;
	}
}
