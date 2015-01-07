package edu.nd.sirs.index;

import java.util.List;
import java.util.Map;

public class IndexDatum
{
	List<String> tokens;
	Map<String, String> resources;
	
	public IndexDatum(List<String> tokens, Map<String, String> resources){
		this.tokens = tokens;
		this.resources = resources;
	}

	public List<String> getTokens() {
		return tokens;
	}
}
