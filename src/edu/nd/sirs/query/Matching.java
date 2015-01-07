package edu.nd.sirs.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.nd.sirs.docs.TextDocument;
import edu.nd.sirs.index.DirectIndex;
import edu.nd.sirs.index.InvertedIndex;
import edu.nd.sirs.index.Lexicon;
import edu.nd.sirs.index.Posting;
import edu.nd.sirs.retrievalmodel.BooleanRM;
import edu.nd.sirs.retrievalmodel.BooleanScoreModifier;
import edu.nd.sirs.retrievalmodel.RetrievalModel;
import edu.nd.sirs.retrievalmodel.ScoreModifier;

public class Matching {

	private static final int RETRIEVED_SET_SIZE = 200;

	private HashMap<String, Integer> queryTermsToMatchList = null;
	private List<ScoreModifier> scoreModifiers = null;

	private int numRetrievedDocs;
	private InvertedIndex index;
	private ResultSet resultSet;
	private RetrievalModel scorer;

	public Matching(RetrievalModel retrievalModel) {
		scoreModifiers = new ArrayList<ScoreModifier>();
		scorer = retrievalModel;
		index = InvertedIndex.getInstance();
	}

	public ResultSet match(Query queryTerms) {
		init(queryTerms);

		numRetrievedDocs = 0;

		final int queryLength = queryTermsToMatchList.size();
		// The posting list iterator array (one per term) and initialization
		List<List<Posting>> postingListArray = new ArrayList<List<Posting>>(
				queryLength);
		for (String terms : queryTermsToMatchList.keySet()) {
			int termId = queryTermsToMatchList.get(terms);

			postingListArray.add(index.getPostings(termId));

			// long docid = postingListArray(i).getId();
			// postingHeap.enqueue((docid << 32) + i);
		}
		boolean targetResultSetSizeReached = false;
		final HashMap<Integer, Hit> accumulators = new HashMap<Integer, Hit>();

		List<Posting> currentPostingList = null;
		float threshold = 0.0f;
		// int scored = 0;

		// while not end of all posting lists
		for (int currentPostingListIndex = 0; currentPostingListIndex < postingListArray
				.size(); currentPostingListIndex++) {

			currentPostingList = postingListArray.get(currentPostingListIndex);
			for (int currentPosting = 0; currentPosting < currentPostingList
					.size(); currentPosting++) {

				int currentDocId = postingListArray
						.get(currentPostingListIndex).get(currentPosting)
						.getDocId();

				// We create a new hit for each new doc id considered
				Hit currentCandidate = accumulators.getOrDefault(currentDocId,
						new Hit(currentDocId));
				accumulators.put(currentDocId, currentCandidate);

				assignScore(currentPostingListIndex, scorer, currentCandidate,
						currentPostingList.get(currentPosting));
			}

			if ((!targetResultSetSizeReached)) {
				if (accumulators.size() >= RETRIEVED_SET_SIZE) {
					targetResultSetSizeReached = true;
				}
			}
		}

		resultSet = new ResultSet(accumulators.values());
		numRetrievedDocs = resultSet.getScores().length;
		finalize(queryTerms);
		return resultSet;

	}

	public int getNumResults() {
		return numRetrievedDocs;
	}

	public void addScoreModifier(ScoreModifier sm) {
		scoreModifiers.add(sm);
	}

	private void init(Query queryTerms) {
		List<String> queryTermStrings = queryTerms.getTerms();
		queryTermsToMatchList = new HashMap<String, Integer>(
				queryTermStrings.size());
		for (String queryTerm : queryTermStrings) {
			Integer t = Lexicon.getInstance().getTermId(queryTerm);
			if (t != -1) {
				queryTermsToMatchList.put(queryTerm, t);
			} else {
				System.err.println("Term not found");
			}
		}
	}

	private void finalize(Query queryTerms) {
		int setSize = Math.min(RETRIEVED_SET_SIZE, numRetrievedDocs);
		if (setSize == 0)
			setSize = numRetrievedDocs;

		resultSet.setExactResultSize(numRetrievedDocs);
		resultSet.setResultSize(setSize);
		resultSet.sort(setSize);

		for (int t = 0; t < scoreModifiers.size(); t++) {
			if (scoreModifiers.get(t)
					.modifyScores(index, queryTerms, resultSet))
				resultSet.sort(resultSet.getResultSize());
		}
	}

	private void assignScore(int i, final RetrievalModel wModels, Hit h,
			final Posting posting) {
		h.updateScore(wModels.score(posting));
		h.updateOccurrence((i < 16) ? (short) (1 << i) : 0);
	}

	public static void main(String[] args) {
		Matching m = new Matching(new BooleanRM());
		m.addScoreModifier(new BooleanScoreModifier());
		ResultSet rs = m.match(new Query("Notre Dame"));
		for (int i : rs.getDocids()) {
			DirectIndex.getInstance().getDoc(i, TextDocument.class);
		}
		System.out.println(rs);
	}
}