package edu.nd.sirs.docs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Document {
	protected String name;
	protected int docId;
	protected int numTokens;
	protected Map<String, String> resources;

	public Document(Integer docId, File file) {
		this.docId = docId;
		this.name = file.getName();
		this.numTokens = 0;
		resources = new HashMap<String, String>();
	}

	public Document(Integer docId, String line) {
		this.docId = docId;
		this.name = "";
		this.numTokens = 0;
		resources = new HashMap<String, String>();
		readFromIndex(line);
	}

	public String getName() {
		return name;
	}

	public int getDocId() {
		return docId;
	}

	public int getNumTokens() {
		return numTokens;
	}

	public String writeToIndex() {
		StringBuffer sb = new StringBuffer();

		sb.append(getDocId() + "\t" + getName() + "\t" + getNumTokens());
		for (Map.Entry<String, String> e : resources.entrySet()) {
			sb.append("\t" + e.getKey() + ":" + e.getValue());
		}
		sb.append("\n");

		return sb.toString();
	}

	public void readFromIndex(String line) {
		String[] s = line.split("\t");
		docId = Integer.parseInt(s[0]);
		name = s[1];
		numTokens = Integer.parseInt(s[2]);
		for (int i = 3; i < s.length; i++) {
			String[] r = s[i].split(":");
			resources.put(r[0], r[1]);
		}
	}

	public abstract List<String> parse(Integer docId, File f);

}
