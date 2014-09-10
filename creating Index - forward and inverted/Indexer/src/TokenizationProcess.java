

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public interface TokenizationProcess {

	public String htmltotext(String docName) throws IOException; // implement using jsoup
	public void assignandwritedocids(String docName) throws IOException;
	public HashMap<Long, HashMap> texttotokens(String text,String regex); 
	// for regex \w+(\.?\w+)* and convert to lower case 
	//How to map tokens to their term id that we get after stemming
	// Map <String, List<String>>
	public HashMap<Long, HashMap> stopping(HashMap<Long, HashMap> tokenswithpositions, String stoplist) throws FileNotFoundException, IOException;
	public void stemming(Map tokenswithoutstopwords); // Porter, Snowball, and KStem stemmers 
}
