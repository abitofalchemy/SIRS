package edu.nd.sirs.index;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.PriorityQueue;

public class RunFile {
	File filename;
	int buffersize;
	long currentPos;
	long length;

	PriorityQueue<Posting> buffer;

	void getFileSize() {
		length = filename.length();
	}

	public RunFile(File file, int bsize) {
		filename = file;
		buffersize = bsize;
		currentPos = 0;
		getFileSize();
		buffer = new PriorityQueue<Posting>(buffersize);
	}

	private boolean fillBuffer() throws IOException {

		boolean readsome = false;
		RandomAccessFile raf = new RandomAccessFile(filename, "r");
		raf.seek(currentPos);
		Posting p;
		int bufsize = buffer.size();

		while ((currentPos < length) && (bufsize < buffersize)) {
			readsome = true;
			StringBuffer sb = new StringBuffer();
			char c = '-';
			while((c=(char)raf.read()) != '\t'){
				sb.append(c);
			}
			int d = Integer.parseInt(sb.toString());
			sb = new StringBuffer();
			while((c=(char)raf.read()) != '\t'){
				sb.append(c);
			}			
			long t = Long.parseLong(sb.toString());
			sb = new StringBuffer();
			while((c=(char)raf.read()) != '\n'){
				sb.append(c);
			}
			int f = Integer.parseInt(sb.toString().trim());
			
			p = new Posting(t, d, f);
			buffer.add(p);

			currentPos = raf.getFilePointer();
			++bufsize;
		}
		raf.close();
		return readsome;
	}

	public Posting getRecord() {
		if (buffer.size() > 0) {
			return buffer.poll();
		} else {
			try {
				if (fillBuffer()) {
					return buffer.poll();
				} else {
					return null;
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

}
