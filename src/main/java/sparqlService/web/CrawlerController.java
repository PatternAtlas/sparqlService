package sparqlService.web;


import com.ontologycentral.ldspider.Crawler;
import com.ontologycentral.ldspider.CrawlerConstants;
import com.ontologycentral.ldspider.frontier.BasicFrontier;
import com.ontologycentral.ldspider.frontier.Frontier;
import com.ontologycentral.ldspider.hooks.content.ContentHandler;
import com.ontologycentral.ldspider.hooks.content.ContentHandlerAny23;
import com.ontologycentral.ldspider.hooks.content.ContentHandlerNx;
import com.ontologycentral.ldspider.hooks.content.ContentHandlerRdfXml;
import com.ontologycentral.ldspider.hooks.content.ContentHandlers;
import com.ontologycentral.ldspider.hooks.sink.Sink;
import com.ontologycentral.ldspider.hooks.sink.SinkCallback;
import com.ontologycentral.ldspider.queue.BreadthFirstQueue;
import com.ontologycentral.ldspider.queue.DummyRedirects;
import com.ontologycentral.ldspider.queue.Redirects;
import com.ontologycentral.ldspider.queue.SpiderQueue;
import com.ontologycentral.ldspider.seen.Seen;


import org.semanticweb.yars.util.CallbackNxBufferedWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Queue;
import org.semanticweb.yars.nx.parser.Callback;


import com.ontologycentral.ldspider.hooks.error.ErrorHandler;
import com.ontologycentral.ldspider.hooks.error.ErrorHandlerLogger;
import com.ontologycentral.ldspider.hooks.fetch.FetchFilterRdfXml;
import com.ontologycentral.ldspider.hooks.links.LinkFilter;
import com.ontologycentral.ldspider.hooks.links.LinkFilterDefault;



import sparqlService.models.DummySeen;

@RestController
@RequestMapping(value = "/api", produces = "application/json")
public class CrawlerController {

	@PostMapping(value = "/crawl")
	public ResponseEntity<String> crawl() throws IOException, URISyntaxException {
		Crawler crawler = new Crawler(1);
		Frontier frontier = new BasicFrontier();
		frontier.add(new URI("http://harth.org/andreas/foaf.rdf"));
		ContentHandler contentHandler = new ContentHandlers(new ContentHandlerRdfXml(), new ContentHandlerAny23());
		crawler.setContentHandler(contentHandler);
		File outputFile = new File(System.getProperty("user.home"), "Desktop/output.txt");
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileWriter fw = new FileWriter(outputFile);
		 BufferedWriter bw = new BufferedWriter(fw);
		Sink sink = new SinkCallback(new CallbackNxBufferedWriter(bw));
		crawler.setOutputCallback(sink);

		int depth = 2;
		int maxURIs = 100;
		boolean includeABox = true;
		boolean includeTBox = false;
		Redirects redirects = new DummyRedirects();
		crawler.evaluateBreadthFirst(frontier, new DummySeen(), redirects, depth, maxURIs, 5, 5, includeABox);
		return new ResponseEntity("hello", HttpStatus.OK);
	}

	@PostMapping(value = "/crawl2")
	public ResponseEntity<String> crawl2() throws IOException, URISyntaxException {
		System.setProperty("http.proxyHost", "localhost");
		System.setProperty("http.proxyPort", "3128");

		Crawler c = new Crawler(1);

		Frontier frontier = new BasicFrontier();
		frontier.add(new URI("http://harth.org/andreas/foaf.rdf"));
		//frontier.add(new URI("http://umbrich.net/foaf.rdf"));

		//frontier.setBlacklist(CrawlerConstants.BLACKLIST);

		LinkFilter lf = new LinkFilterDefault(frontier);

		c.setFetchFilter(new FetchFilterRdfXml());
		c.setLinkFilter(lf);

		//c.setLinkFilter(new LinkFilterDummy());

		ErrorHandler eh = new ErrorHandlerLogger(null, null);
		c.setErrorHandler(eh);

		File outputFile = new File(System.getProperty("user.home"), "Desktop/output.txt");
		if (!outputFile.exists()) {
			outputFile.createNewFile();
		}
		FileWriter fw = new FileWriter(outputFile);
		BufferedWriter bw = new BufferedWriter(fw);
		Callback cb = new CallbackNxBufferedWriter(bw);
		SinkCallback sc = new SinkCallback(cb, true);

		c.setOutputCallback(sc);

		c.evaluateBreadthFirst(frontier, new DummySeen(), new DummyRedirects(), 1, -1, -1, 5, false);
		return new ResponseEntity("hello", HttpStatus.OK);
	}
}
