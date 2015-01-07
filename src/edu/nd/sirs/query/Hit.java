package edu.nd.sirs.query;

public class Hit {

	private int docid;
	private float score;
	private short occurrence;

	public Hit(int docid) {
		this.docid = docid;
	}

	public int getDocId() {
		return docid;
	}

	public float getScore() {
		return score;
	}

	public short getOccurrence() {
		return occurrence;
	}

	public void updateScore(double update) {
		this.score += update;
	}

	public void updateOccurrence(short update) {
		this.occurrence |= update;
	}

}
