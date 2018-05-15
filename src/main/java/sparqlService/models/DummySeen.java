package sparqlService.models;

import com.ontologycentral.ldspider.seen.Seen;

import java.net.URI;
import java.util.Collection;

public class DummySeen implements Seen {
	public DummySeen() {

	}
	@Override
	public boolean hasBeenSeen(URI uri) {
		return false;
	}

	@Override
	public boolean add(Collection<URI> collection) {
		return false;
	}

	@Override
	public boolean add(URI uri) {
		return false;
	}
}
