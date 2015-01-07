package edu.nd.sirs.index;

public class Posting implements Comparable<Posting> {

	long term;
	int doc;
	int frequency;

	public Posting(long _termId, int _docId, int _frequency) {
		term = _termId;
		doc = _docId;
		frequency = _frequency;
	}

	public long getTermId() {
		return term;
	}

	public int getDocId() {
		return doc;
	}

	public int getFrequency() {
		return frequency;
	}

	public Posting(String line) {
		String[] s = line.split("\t");
		term = Long.parseLong(s[0]);
		doc = Integer.parseInt(s[1]);
		frequency = Integer.parseInt(s[2]);
	}

	public int compareTo(Posting o) {
		if (term < o.term) {
			return -1;
		} else if (term == o.term) {
			if (doc < o.doc) {
				return -1;
			} else if (doc == o.doc) {
				return 0;
			} else {
				return 1;
			}
		} else {
			return 1;
		}
	}

}
