package edu.nd.sirs.retrievalmodel;

import edu.nd.sirs.index.Posting;

public interface RetrievalModel {
	public float score(Posting q);
}
