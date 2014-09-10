import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class InvertedIndexCreator {
	public static LinkedHashMap<String, LinkedHashMap<String, List<String>>> invertedIndex= new LinkedHashMap<>();
	// LinkedHashMap<termId, LinkedHashMap<DocId, List<term Pos Offset delta>>>
	final static int constanttermsindexedmemory = 50000;
	static int countfortermindex = 0;
	public static long byteoffset[];
	static long t1;
	/**
	 * @param args
	 */
	static {
		//t1 = System.currentTimeMillis()/1000;
		System.out.println("\nProgram Running . . . : It will take some minutes  for the program to run completely -- Please wait");
	}
	

	public static void main(String[] args) throws NumberFormatException,
			IOException {
		// TODO Auto-generated method stub

		BufferedReader br = null;
		String sLine;
		br = new BufferedReader(new FileReader("doc_index.txt"));
		HashSet<String> termIdcheck = new HashSet<String>();
		int count = 0;
		while ((sLine = br.readLine()) != null) {
			LinkedHashMap<String, List<String>> inner = new LinkedHashMap<>();
			List l = new ArrayList<>();
			// System.out.println(sCurrentLine);
			String[] parts = sLine.split("\t");
			String part0_docId = parts[0];
			String part1_termId = parts[1];
			boolean b = termIdcheck.add(parts[1]);
			if (b) {
				inner = new LinkedHashMap<>();
			} else {// If the term already exists then
				inner = invertedIndex.get(part1_termId);
				/*
				 * String key = "0"; for(String k : inner.keySet()){ key = k; }
				 * Long docoffset = Long.parseLong(parts[0]) -
				 * Long.parseLong(key); //part0_docId = docoffset.toString();
				 */}
			Long prevoffset = Long.parseLong(parts[2]);
			l.add(parts[2]);
			for (int i = 3; i < parts.length; i++) {
				Long offset = Long.parseLong(parts[i]) - prevoffset;
				l.add(offset.toString());
				prevoffset = Long.parseLong(parts[i]);
			}
			inner.put(part0_docId, l);
			invertedIndex.put(part1_termId, inner);
			countfortermindex = countfortermindex +1;
			LinkedHashMap<String, LinkedHashMap<String, List<String>>> invertedIndex= new LinkedHashMap<>();
			if (countfortermindex%constanttermsindexedmemory == 0){
				invertedIndex = new LinkedHashMap<>();
			}
			/*
			 * count = count + 1; if (count == 2) break;
			 */
		}
		// System.out.println(invertedIndex);
		//System.out.println(System.currentTimeMillis() / 1000 - t1);
		writetermindex();
		writeterminfo();

	}
	
	public static void writetermindex() throws IOException{
		File file = new File("term_index.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		byteoffset = new long[invertedIndex.size()+1];
		int byteoffsetcounter = 0;
		long sumforbyteoffset = 0;
		byteoffset[0] = 0;
		for (Entry e: invertedIndex.entrySet()){
			StringBuffer temp = new StringBuffer();
			LinkedHashMap<String, List<String>> inner = new LinkedHashMap<>();
			temp.append(e.getKey());
			temp.append("\t");
			inner = (LinkedHashMap<String, List<String>>) e.getValue();
			Long prevdocoffset = 0L;
			for(Entry e1 : inner.entrySet()){
				Long docoffset = Long.parseLong(e1.getKey().toString());
				temp.append(docoffset - prevdocoffset);
				temp.append(":");
				List l = (List) e1.getValue();
				int count = 0;
				for(Object s : l){
					count = count +1;
					if (count == 1){
						temp.append(s.toString());
						temp.append("\t");
					}
					else{
						temp.append("0:");
						temp.append(s.toString());
						temp.append("\t");
					}
				}
				prevdocoffset = docoffset;
			}
			temp.append("\n");
			bw.write(temp.toString());
			
			byte[] utf8Bytes = temp.toString().getBytes("UTF-8");
			sumforbyteoffset = sumforbyteoffset + utf8Bytes.length;
			byteoffsetcounter = byteoffsetcounter +1;
			byteoffset[byteoffsetcounter] = sumforbyteoffset;
		}
		bw.close();
		//System.out.println(System.currentTimeMillis()/1000-t1);
	}
	
	public static void writeterminfo() throws IOException{
		File file = new File("term_info.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		int byteoffsetcounter = 0;
		for (Entry e: invertedIndex.entrySet()){
			StringBuffer temp = new StringBuffer();
			LinkedHashMap<String, List<String>> inner = new LinkedHashMap<>();
			temp.append(e.getKey());
			temp.append("\t");
			temp.append(byteoffset[byteoffsetcounter]);
			byteoffsetcounter = byteoffsetcounter +1;
			temp.append("\t");
			inner = (LinkedHashMap<String, List<String>>) e.getValue();
			int documentcount = inner.entrySet().size();
			long corpuscount = 0;
			for(Entry e1 : inner.entrySet()){
				List l = (List) e1.getValue();
				corpuscount = corpuscount + l.size();
			}
			temp.append(corpuscount);
			temp.append("\t");
			temp.append(documentcount);
			temp.append("\n");
			bw.write(temp.toString());
		}
		bw.close();
		//System.out.println(System.currentTimeMillis()/1000-t1);
		System.out.println("Program Ran Successfully");
	}

}
