

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;


public class Tokenizer {
	
	public static LinkedHashMap<Long, String> docids = new LinkedHashMap<>();
	public static LinkedHashMap<String, Long> termids_afterstemming = new LinkedHashMap<>();
	public static HashSet<String> evaluniqueterms = new HashSet<>();
	public static LinkedHashMap<Long, HashMap> finaldocumentandPositionSet = new LinkedHashMap<>();
	final static int constantdocmemory = 1000;
	final static int constanttermmemory = 50000;
	final static int constanttermpositionmemory = 1000;
	static int countforpositionset = 0;
	// LinkedHashMap<docId, HashMap<termId, positions of termId in that document>> 
	/*
	 * Logic no.of docs count +1000 = termId start position
	 */
	// datastructure of stoplist
	
	public Tokenizer() {
		// TODO Auto-generated constructor stub
	}
	
	public void initialize(String docname) throws IOException{
		assignandwritedocids(docname);
		String text = htmltotext(docname);
		LinkedHashMap<Long, HashMap> documentandPositionSet = texttotokens(text,"\\w+(\\.?\\w+)*");
		documentandPositionSet = stopping(documentandPositionSet, "stoplist.txt");
		/*for (Object i : documentandPositionSet.entrySet()) {
			System.out.println(i);
		}*/
		documentandPositionSet = stemming(documentandPositionSet);
		HashSet<String> evalduptermsperdocument = new HashSet<>();
		HashMap<Integer, String> positionSet = documentandPositionSet.get(Indexer.docID);
		HashMap<String, List<String>> newpositionSet = new HashMap<>();
		for (Entry e : positionSet.entrySet()){
			List l = new ArrayList<String>();
			boolean b = evalduptermsperdocument.add(e.getValue().toString());
			if (b){
				l.add(e.getKey().toString());
				newpositionSet.put(e.getValue().toString(), l);
			}else{
				l = newpositionSet.get(e.getValue().toString());
				l.add(e.getKey().toString());
				newpositionSet.put(e.getValue().toString(), l);
			}
			
		}
		this.finaldocumentandPositionSet.put(Indexer.docID, newpositionSet);
		countforpositionset = countforpositionset+1;
		LinkedHashMap<Long, HashMap> finaldocumentandPositionSet = new LinkedHashMap<>();
		if (countforpositionset%constanttermpositionmemory == 0){
			finaldocumentandPositionSet = this.finaldocumentandPositionSet;
			finaldocumentandPositionSet = new LinkedHashMap<>();
		}
		
		
		
/*		for (Object i : documentandPositionSet.entrySet()) {
			System.out.println(i);
		}*/
	}
	
	static String readFile(String path, Charset encoding) 
			  throws IOException 
			{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return encoding.decode(ByteBuffer.wrap(encoded)).toString();
			}
	
	public String htmltotext(String docName) throws IOException {
		
		String s = readFile(docName,StandardCharsets.UTF_8);
		
	
		int firstindex = 0;
		//Removing duplicate slashes
		String g;
		/* Implementation of bypassing the header */
		firstindex = s.indexOf("\n\n", 0);
		if (firstindex == -1) {
			firstindex = s.indexOf("<!DOCTYPE", 0);
			if (firstindex == -1) {
				firstindex = s.indexOf("<html", 0);
				if (firstindex == -1) {
					firstindex = s.indexOf("<HTML", 0);
					if (firstindex == -1) {
						firstindex = s.indexOf("text/plain", 0);
						if (firstindex == -1) {
							firstindex = s.indexOf("Connection: close", 0);
						}
					}

				}
			}
			g = s.substring(firstindex, s.length());
		} else {
			g = s.substring(firstindex, s.length());
		}
		Document doc  = Jsoup.parse(g);
		return doc.text();
	}

	
	public void assignandwritedocids(String docName) throws IOException {
		// TODO Auto-generated method stub
		Indexer.docID = Indexer.docID + 1;
		//System.out.println(Indexer.docID);
		//System.out.println(docName);
		docName = docName.substring(docName.lastIndexOf("/")+1);
		this.docids.put(Indexer.docID, docName);
		LinkedHashMap<Long, String> docids = new LinkedHashMap<>();
		if (Indexer.docID%constantdocmemory == 0){
			docids = this.docids;
			docids = new LinkedHashMap<>();
		}
	}

	
	public LinkedHashMap<Long, HashMap> texttotokens(String text, String regex) {
		// TODO Auto-generated method stub

		Pattern p = Pattern.compile(regex);
		StringTokenizer st = new StringTokenizer(text.toLowerCase());
		HashMap<Integer, String> positionSet = new HashMap<>();
		LinkedHashMap<Long, HashMap> documentandPositionSet = new LinkedHashMap();
		int position = 0;
		String s1 = "";
		//System.out.println(text);
		while (st.hasMoreTokens()) {
			s1 = "";
			s1 = st.nextToken();
//			/System.out.println(s1);

			Matcher m = p.matcher(s1);
			while (m.find()) {
				position = position + 1;
				String s2 = m.group().toString();
				positionSet.put(position, s2);
				// System.out.println(s1);
			}
		}
		documentandPositionSet.put(Indexer.docID, positionSet);
		return documentandPositionSet;
	}


	
	public LinkedHashMap<Long, HashMap> stopping(LinkedHashMap<Long, HashMap> documentandPositionSet, String stoplist) throws IOException {
		// TODO Auto-generated method stub
		HashSet <String> hs = new HashSet<String>(); 
		BufferedReader br = null;
		String sCurrentLine;
		 
		br = new BufferedReader(new FileReader(stoplist));
		while ((sCurrentLine = br.readLine()) != null) {
			hs.add(sCurrentLine);
		}
		HashMap<Integer, String> positionSet = documentandPositionSet.get(Indexer.docID);
		HashMap<Integer, String> positionSetnew = new HashMap<>();
		
		for (Entry<Integer, String> o: positionSet.entrySet()){
			if(!hs.contains(o.getValue())){
				positionSetnew.put(o.getKey(), o.getValue());
			}
			
		}
		documentandPositionSet.put(Indexer.docID, positionSetnew);
		return documentandPositionSet;
	}

	
	public LinkedHashMap<Long, HashMap> stemming(LinkedHashMap<Long, HashMap> documentandPositionSet) {
		// TODO Auto-generated method stub
		SnowballStemmer stemmer = (SnowballStemmer) new englishStemmer();
		HashMap<Integer, String> positionset = new HashMap<>();
		HashMap<String , List<Long>> newPosSet  = new HashMap<>();
		
		for(Entry e : documentandPositionSet.entrySet()){
			positionset = (HashMap<Integer, String>) e.getValue();
			//List<Long> l = new ArrayList<>();
			for (Entry e1: positionset.entrySet()){
				stemmer.setCurrent(e1.getValue().toString());
				stemmer.stem();
				String s = stemmer.getCurrent().toString();
				positionset.put((Integer) e1.getKey(), s);
				boolean b = evaluniqueterms.add(s);
				if (b){
					this.termids_afterstemming.put(s, ++Indexer.termId);
					LinkedHashMap<String, Long> termids_afterstemming = new LinkedHashMap<>();
					if (Indexer.termId%constanttermmemory == 0){
						termids_afterstemming = this.termids_afterstemming;
						termids_afterstemming = new LinkedHashMap<>();
					}
				}
			}
			documentandPositionSet.put((Long) e.getKey(), positionset);
		}
		return documentandPositionSet;
	}

}
