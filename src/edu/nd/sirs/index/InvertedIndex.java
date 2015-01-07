package edu.nd.sirs.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class InvertedIndex {
	private static InvertedIndex me = null;
	private long[] offsets;
	private RandomAccessFile idx;

	private InvertedIndex() {
		try {
			idx = new RandomAccessFile("./data/idx.txt", "r");
			loadOffsets();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void loadOffsets() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"./data/idx_term_offset.txt"));
		String line = br.readLine(); // number of terms
		int terms = Integer.parseInt(line);
		offsets = new long[terms];

		for (int term = 0; (line = br.readLine()) != null; term++) {
			offsets[term] = Long.parseLong(line);
		}
		br.close();
	}

	public static InvertedIndex getInstance() {
		if (me == null) {
			me = new InvertedIndex();
		}

		return me;
	}

	public List<Posting> getPostings(int termid) {	
		List<Posting> postings = new ArrayList<Posting>();
		long offset = offsets[termid];
		try {
			idx.seek(offset);
			while (true) {
				String line = idx.readLine();
				if (line == null || line.isEmpty())
					break;
				Posting p = new Posting(line);
				if (termid != p.term)
					break;
				postings.add(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return postings;
	}
	
	public static void main(String[] args){
		InvertedIndex idx = InvertedIndex.getInstance();
		List<Posting> x = idx.getPostings(100);
		System.out.println(x);
	}

}
