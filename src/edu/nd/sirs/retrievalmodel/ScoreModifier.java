package edu.nd.sirs.retrievalmodel;


import edu.nd.sirs.index.InvertedIndex;
import edu.nd.sirs.query.ResultSet;
import edu.nd.sirs.query.Query;

public interface ScoreModifier {
	boolean modifyScores(InvertedIndex index, Query queryTerms,
			ResultSet resultSet);
}
