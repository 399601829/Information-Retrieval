import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;


public class IndexReader {
	public static LinkedHashMap<String, String> docids = new LinkedHashMap<>(); 
	public static LinkedHashMap<String, String> termids = new LinkedHashMap<>();
	public static LinkedHashMap<String, List<String>> terminfo = new LinkedHashMap<>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br = null;
		String sLine;
		
		if (args.length>0){
		
		br = new BufferedReader(new FileReader("docids.txt"));
		
		while ((sLine = br.readLine()) != null) {
			String[] parts = sLine.split("\t");
			docids.put(parts[1], parts[0]);
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("--doc")){
			//clueweb12-0206wb-76-27846
			readdocumentIndex(args[1]);
			return;
		}
		
		br = new BufferedReader(new FileReader("termids.txt"));
		while ((sLine = br.readLine()) != null) {
			String[] parts = sLine.split("\t");
			termids.put(parts[1], parts[0]);
		}
		
		br = new BufferedReader(new FileReader("term_info.txt"));

		while ((sLine = br.readLine()) != null) {
			String[] parts = sLine.split("\t");
			List l = new ArrayList<String>();
			l.add(parts[1]);
			l.add(parts[2]);
			l.add(parts[3]);
			terminfo.put(parts[0], l);
		}
		
		if (args.length == 2 && args[0].equalsIgnoreCase("--term")){
			//search
			readTermIndex(args[1]);
			return;
		}
		
		if (args.length == 4 && args[0].equalsIgnoreCase("--term") && args[2].equalsIgnoreCase("--doc")){
			//search clueweb12-0000tw-13-04988
			readdocumentandTermIndex(args[1],args[3]);
			return;
		}
		
		System.out.println("Pass Correct arguments");
			
		
		}
		
	}
	
	public static void readdocumentIndex(String docName) throws NumberFormatException, IOException{
		String sLine = null;
		System.out.println("Listing for document: "+ docName);
		if(docids.get(docName) == null){
			System.out.println("Invalid document Name: Try again");
		}
		System.out.println("DOCID: "+ docids.get(docName));
		File file = new File("doc_info.txt");
		sLine = FileUtils.readLines(file).get(Integer.parseInt(docids.get(docName))-1);
		String[] parts = sLine.split("\t");
		int distinctterms = Integer.parseInt(parts[2]);
		int offsettodoc_index = Integer.parseInt(parts[1]) - 1;
		System.out.println("Distinct terms: "+distinctterms);
		file = new File("doc_index.txt");
		List l = FileUtils.readLines(file);
		
		//System.out.println(l.get(offsettodoc_index));
		int len = 0;
		for (int i = offsettodoc_index ; i<offsettodoc_index+distinctterms ; i++){
			String s = (String) l.get(i);
			String[] part = s.split("\t");
			len = len + (part.length - 2);	
		}
		System.out.println("Total terms: "+len);
	}
	
	public static void readTermIndex(String termName) throws IOException{
		System.out.println("Listing for term: "+ termName);
		SnowballStemmer stemmer = (SnowballStemmer) new englishStemmer();
		stemmer.setCurrent(termName);
		stemmer.stem();
		String s = stemmer.getCurrent().toString();
		if (termids.get(s) == null){
			System.out.println("Invalid term name: Try again");
		}
		System.out.println("TERMID: "+ termids.get(s));
		List getterminfo = terminfo.get(termids.get(s));
		System.out.println("Number of documents containing term: "+getterminfo.get(2));
		System.out.println("Term frequency in corpus: "+getterminfo.get(1));
		System.out.println("Inverted list offset: "+getterminfo.get(0));
		
	}
	
	public static void readdocumentandTermIndex(String termName, String docName) throws NumberFormatException, IOException{
		RandomAccessFile file = new RandomAccessFile("term_index.txt", "r");
		long docid = Long.parseLong(docids.get(docName));
		long tempdocid = 0L;
		System.out.println("Inverted list for term: "+termName);
		SnowballStemmer stemmer = (SnowballStemmer) new englishStemmer();
		stemmer.setCurrent(termName);
		stemmer.stem();
		String s = stemmer.getCurrent().toString();
		if (termids.get(s) == null){
			System.out.println("Invalid term name: Try again");
		}
		
		System.out.println("TERMID: "+termids.get(s));
		if(docids.get(docName) == null){
			System.out.println("Invalid document Name: Try again");
		}
		System.out.println("DOCID: "+ docid);
		System.out.println("In document: "+docName);
		List l = terminfo.get(termids.get(s));
		//System.out.println(l);
		file.seek(Long.parseLong(l.get(0).toString()));
		//System.out.println(file.readLine());
		String debug = file.readLine();
		//System.out.println(debug);
		String[] parts = debug.split("\t");
		//System.out.println(parts);
		List poslist = null;
		// Reverse Engineering
		for (int i = 1; i < parts.length; i++) {
			String[] temp = parts[i].split(":");
			tempdocid = tempdocid + Long.parseLong(temp[0]);
			poslist = new ArrayList<String>(); 
			if (tempdocid == docid) {
				int k = i;
				long possum = 0;
				while (tempdocid == docid) {
					possum = possum + Long.parseLong(temp[1]); 
					poslist.add(possum);
					temp = parts[k + 1].split(":");
					tempdocid = tempdocid + Long.parseLong(temp[0]);
					k = k + 1;
				}
				break;
			}
		}
		if (poslist.size() == 0) {
			System.out.println("term: "+termName+" is not contained in document: "+docName);
		} else {
			System.out.println("Term frequency in document: " + poslist.size());
			System.out.print("Positions: ");
			for (int j = 0; j < poslist.size(); j++) {
				System.out.print(poslist.get(j));
				if (j != poslist.size() - 1) {
					System.out.print(", ");
				}
			}
		}
	}
}
