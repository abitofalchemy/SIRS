package edu.nd.sirs.index;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.TreeMap;

import edu.nd.sirs.docs.Document;
import edu.nd.sirs.docs.TextDocument;

public class Indexer {

	private static final Integer RUN_SIZE = 10000;
	private static final Boolean COMPRESS = false;

	private int wordId = 0;
	private int docId = 0;
	private List<Posting> run;
	private int runNumber = 0;

	private TreeMap<String, Integer> voc = new TreeMap<String, Integer>();

	private void indexDirectory(File[] filesToIndex) {
		docId = 0;

		PrintWriter docWriter;
		PrintWriter docWriterOffset;
		try {
			docWriter = new PrintWriter("./data/doc_idx.txt");
			docWriterOffset = new PrintWriter("./data/doc_idx_offset.txt");

			// start the first run
			run = new ArrayList<Posting>();
			int written = 0;
			for (File file : filesToIndex) {
				Document doc = new TextDocument(docId, file);				
				List<String> tokens = doc.parse(docId, file);
				index(tokens);
				docWriterOffset.write(written + "\n");
				String idxable = doc.writeToIndex();
				docWriter.write(idxable);
				written += idxable.length();
				docId++;
			}
			docWriter.close();
			docWriterOffset.close();

			// If there is something yet in the last run, sort it and store
			if (run.size() > 0) {
				storeRun();
			}

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		try {
			mergeRuns();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Output the vocabulary
		try {
			outputLexicon();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void outputLexicon() throws FileNotFoundException {
		PrintWriter lexFile = new PrintWriter("./data/lex.txt");
		for (Entry<String, Integer> x : voc.entrySet()) {
			lexFile.println(x.getKey() + "\t" + x.getValue());
		}
		lexFile.close();
	}

	private void mergeRuns() throws FileNotFoundException {
		// Everything looking good! Now to write the vocabulary
		/*
		 * vocabulary::iterator it; outFile.open("voc/vocabulary.txt"); for (it
		 * = voc.begin(); it != voc.end(); ++it) { outFile << it->first << ";"
		 * << it->second << endl; } outFile.close();
		 */

		// Create the heap
		PriorityQueue<MergePosting> mergeHeap = new PriorityQueue<MergePosting>();
		// Load the sorted runs using mmap
		List<RunFile> rfv = new ArrayList<RunFile>();
		File rftmp;
		String filename;
		Posting ocurr;
		MergePosting ro;
		for (int i = 0; i < runNumber; ++i) {
			filename = "./data/runs/run" + i;
			rfv.add(new RunFile(new File(filename), RUN_SIZE / runNumber));
			// rfv[0] = rftmp;
			// get the first element and put it in the heap
			ocurr = rfv.get(i).getRecord();
			if (ocurr == null) {
				System.err.println("Error: Record was not found.");
				return;
			}
			ro = new MergePosting(ocurr, i);
			mergeHeap.add(ro);
		}
		long currentTerm = 0l;
		long currentTermOffset = 0l;
		int currentDoc = 0;
		int gap;
		PrintWriter outFile = new PrintWriter("./data/idx.txt");
		PrintWriter tosFile = new PrintWriter("./data/idx_term_offset.txt");
		String wid = wordId + "\n";
		tosFile.print(wid);
		tosFile.println(0);

		MergePosting first;
		while (!mergeHeap.isEmpty()) {
			first = mergeHeap.poll();

			// Get a new RunOcurrence from the same run and
			// put it in the heap, if possible
			ocurr = rfv.get(first.run).getRecord();
			if (ocurr != null) {
				ro = new MergePosting(ocurr, first.run);
				mergeHeap.add(ro);
			}
			// Calculate the document gap
			if (first.term > currentTerm) {
				// New term, new gaps...
				currentDoc = 0;
			}
			// gap = first.doc - currentDoc;
			// currentDoc = first.doc;
			// first.doc = gap;
			// TODO Elias gamma compression will happend HERE
			// first.document = EliasGamma::Compress(first.document); //??
			// Saving to the file
			if (first.term > currentTerm) {
				tosFile.println(currentTermOffset);
				currentTerm = first.term;
			} else if (first.term < currentTerm) {
				System.err.println("This shouldn't happen.");
			}
			if (COMPRESS) {
				//String tmp = EliasGamma.eliasGammaCode(first.doc);
				//outFile.write(tmp);
				//outFile.write(first.frequency);
			} else {
				String s = new String(currentTerm + "\t" + first.doc + "\t"
						+ first.frequency + "\n");
				outFile.print(s);
				currentTermOffset += s.getBytes().length;
			}
		}
		outFile.close();
		tosFile.close();
	}

	private void index(List<String> tokens) {
		HashMap<Integer, Posting> lVoc = new HashMap<Integer, Posting>();		
		for (String token : tokens) {
			index(token, docId, lVoc);
		}

		for (Posting p : lVoc.values()) {
			if (run.size() < RUN_SIZE) {
				run.add(p);
			} else {
				storeRun();
				run.add(p);
			}
		}

	}

	private void storeRun() {
		// creating the output file
		try {
			long runId = getRunNumber();
			File outName = new File("./data/runs/run" + runId);
			if (!outName.getParentFile().exists()) {
				outName.getParentFile().mkdir();
			}
			if (outName.exists()) {
				outName.delete();
			}
			PrintWriter outFile = new PrintWriter(outName);
			// sorting the run
			Collections.sort(run);
			// Storing it

			for (Posting p : run) {
				// end of vector model data gathering
				outFile.println(p.doc + "\t" + p.term + "\t" + p.frequency);
			}
			outFile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		run.clear();
	}

	private long getRunNumber() {
		++runNumber;
		return runNumber - 1;
	}

	private int getNewId() {
		++wordId;
		return wordId - 1;
	}

	private void index(String token, int docId, HashMap<Integer, Posting> lVoc) {
		int termId;
		if (!voc.containsKey(token)) {
			termId = getNewId();
			voc.put(token, termId);
		} else {
			termId = voc.get(token);
		}

		if (!lVoc.containsKey(termId)) {
			Posting p = new Posting(termId, docId, 1);
			lVoc.put(termId, p);
		} else {
			Posting p = lVoc.get(termId);
			++p.frequency;
			// do we need this?
			lVoc.put(termId, p);
		}
	}

	private File[] getFiles(File dir) {
		if (!dir.isDirectory()) {
			System.err.println(dir + " not a directory of files.");
			System.exit(1);
		}
		return dir.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return new File(dir, name).isFile();
			}

		});
	}

	public static void main(String[] args) {
		Indexer idxr = new Indexer();
		File[] filesToIndex = idxr.getFiles(new File(args[0]));
		idxr.indexDirectory(filesToIndex);

	}

}
