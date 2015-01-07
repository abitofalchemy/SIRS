package edu.nd.sirs.parser;

import java.io.FileReader;
import java.util.List;

public interface ITokenizer {

	List<String> tokenize(String str);

	List<String> tokenize(FileReader fr);

}
