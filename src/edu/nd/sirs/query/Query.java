package edu.nd.sirs.query;

import java.util.ArrayList;
import java.util.List;

import edu.nd.sirs.parser.ITokenizer;
import edu.nd.sirs.parser.WhitespaceTextTokenizer;

public class Query{

	private String queryString;
	private ITokenizer tokenizer;
	private List<String> terms;

	public Query(String queryString) {
		this(new WhitespaceTextTokenizer(), queryString);
	}

	public Query(ITokenizer tok, String queryString) {
		this.tokenizer = tok;
		this.queryString = queryString;
		this.terms = new ArrayList<String>();
		for (String s : parse()) {
			terms.add(s);
		}
	}

	public List<String> getTerms() {
		return terms;
	}

	public Iterable<String> parse() {
		return tokenizer.tokenize(queryString);
	}

}
