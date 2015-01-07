package edu.nd.sirs.index;

import java.io.IOException;
import java.io.RandomAccessFile;

public class Lexicon {
	private static Lexicon me = null;
	private RandomAccessFile lex;
	private long length;

	private Lexicon() {
		try {
			lex = new RandomAccessFile("./data/lex.txt", "r");
			length = lex.length();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Lexicon getInstance() {
		if (me == null) {
			me = new Lexicon();
		}

		return me;
	}

	public int getTermId(String term) {
		long low = 0;
		long high = length;
		long cur = high;
		int x = -3;
		do {
			if (high - low < 200) {
				return scanToFind(low, high, term);
			}
			if (x == -3) {
				cur = low + (high - low) / 2;
			} else if (x == -1) {
				cur = low + (high - low) / 2;
			}
			x = compareTo(cur, term);
			if (x >= 0) {
				return x;
			} else if (x == -3) {
				low = cur;
			} else { // -1
				high = cur;
			}
		} while (x < 0);

		return -1;
	}

	private int scanToFind(long pos, long high, String term) {
		try {
			lex.seek(pos);
			if (pos < 200) {
				pos = 0;
			} else {
				lex.readLine();
			}
			String l = "";
			while ((l = lex.readLine()) != null) {
				String[] line = l.split("\t");
				if (line[0].equals(term)) {
					return Integer.parseInt(line[1]);
				}

				if (lex.getFilePointer() > high) {
					return -1;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

	private int compareTo(long pos, String term) {
		try {
			lex.seek(pos);
			lex.readLine(); // get to the end of the line
			String[] line = lex.readLine().split("\t");
			if (line[0].equals(term)) {
				return Integer.parseInt(line[1]);
			} else {
				return line[0].compareTo(term) < 0 ? -3 : -1;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return -3;
	}

	public static void main(String[] args) {
		Lexicon lex = Lexicon.getInstance();
		System.out.println(lex.getTermId("weninger"));
	}
}
