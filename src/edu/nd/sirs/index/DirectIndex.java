package edu.nd.sirs.index;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import edu.nd.sirs.docs.Document;
import edu.nd.sirs.docs.TextDocument;

public class DirectIndex {
	private static DirectIndex me = null;
	private List<Long>  offsets;
	private RandomAccessFile idx;

	private DirectIndex() {
		try {
			idx = new RandomAccessFile("./data/doc_idx.txt", "r");
			loadOffsets();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void loadOffsets() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(
				"./data/doc_idx_offset.txt"));
		String line = br.readLine(); // number of terms
		offsets = new ArrayList<Long>();

		offsets.add(0l);
		while ((line = br.readLine()) != null) {
			offsets.add(Long.parseLong(line));
		}
		br.close();
	}

	public static DirectIndex getInstance() {
		if (me == null) {
			me = new DirectIndex();
		}

		return me;
	}

	public Document getDoc(int docid, Class<? extends Document> d) {
		try {
			long offset = offsets.get(docid);
			idx.seek(offset);
			String line = idx.readLine();
			Constructor<? extends Document> c = d.getDeclaredConstructor(new Class[] {Integer.class, String.class});
			return d.cast(c.newInstance(new Object[] {docid, line}));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		DirectIndex idx = DirectIndex.getInstance();
		idx.getDoc(85, TextDocument.class);
	}

}
