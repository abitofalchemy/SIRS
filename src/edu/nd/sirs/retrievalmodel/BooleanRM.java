package edu.nd.sirs.retrievalmodel;

import edu.nd.sirs.index.Posting;

public class BooleanRM implements RetrievalModel {

	public float score(Posting q) {
		return 1.0f;
	}

}
