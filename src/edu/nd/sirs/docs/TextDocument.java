package edu.nd.sirs.docs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.nd.sirs.parser.ITokenizer;
import edu.nd.sirs.parser.WhitespaceTextTokenizer;

public class TextDocument extends Document {

	public TextDocument(Integer docId, File file) {
		super(docId, file);
	}
	
	public TextDocument(Integer docId, String line) {
		super(docId, line);
	}

	@Override
	public List<String> parse(Integer docId, File f) {
		ITokenizer tokenizer = new WhitespaceTextTokenizer();
		List<String> toks = new ArrayList<String>();

		try {
			FileReader fr = new FileReader(f);
			toks = tokenizer.tokenize(fr);
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		numTokens = toks.size();
		
		return toks;
	}

}
