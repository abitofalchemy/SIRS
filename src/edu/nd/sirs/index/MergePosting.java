package edu.nd.sirs.index;

import edu.nd.sirs.index.Posting;

public class MergePosting extends Posting{

	int run;

	public MergePosting(Posting p, int r) {
		super(p.term, p.doc, p.frequency);
		run = r;
	}

}
