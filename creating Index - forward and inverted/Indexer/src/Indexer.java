
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Indexer {
	static long docID = 0;
	static long termId = 0;
	static long t1;
	/**
	 * @param args
	 * @throws IOException 
	 */
	static {
		//t1 = System.currentTimeMillis()/1000;
		System.out.println("\nProgram Running . . . : It will take some minutes  for the program to run completely -- Please wait");
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		String rootdir = System.getProperty("user.dir");
		File folder = new File(rootdir+"/corpus/");
		File[] listOfFiles = folder.listFiles();
		Tokenizer t = new Tokenizer();
		for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        String docName = rootdir+"/corpus/"+listOfFiles[i].getName();
		        //System.out.println(docName);
		        t.initialize(docName);
		      }
		}
		
		writedocid();
		writetermids();
		writeforwardindex();
		writedocinfo();
	}
	
	public static void writedocid() throws IOException{
		/*Writing docid to the file*/
		File file = new File("docids.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (Entry e: Tokenizer.docids.entrySet()){
			bw.write(e.getKey() +"\t"+ e.getValue()+"\n");
		}
		bw.close();
	}
	
	public static void writetermids() throws IOException{
		/*Writing termids to the file*/
		File file = new File("termids.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		for (Entry e: Tokenizer.termids_afterstemming.entrySet()){
			
			bw.write(e.getValue() +"\t"+  e.getKey()+"\n");
		}
		bw.close();
		//System.out.println(System.currentTimeMillis()/1000 - t1);
	}
	
	public static void writeforwardindex() throws IOException{
		/*Writing forwardindex to the file*/
		File file = new File("doc_index.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
				
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuffer temp = new StringBuffer();
		for (Entry e : Tokenizer.finaldocumentandPositionSet.entrySet()) {
			HashMap<String, List<String>> termsindoc = (HashMap<String, List<String>>) e.getValue(); // Termid per document
			temp = new StringBuffer();
			for (Entry e1 : termsindoc.entrySet()) {
				temp.append(e.getKey().toString()); // docid
				temp.append("\t");
				temp.append(Tokenizer.termids_afterstemming.get(e1.getKey().toString()));
				temp.append("\t");
				ArrayList<String> l = (ArrayList<String>) e1.getValue();
				for (String lo : l) {
					temp.append(lo);
					temp.append("\t");
				}
				temp.append("\n");
			}
			bw.write(temp.toString());
		}
		
		bw.close();
		
		//System.out.println(System.currentTimeMillis()/1000 - t1);
	}
	
	public static void writedocinfo() throws IOException{
		
		
		File file = new File("doc_info.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		StringBuffer temp = new StringBuffer();
		Long prevoffset = 1L;
		for (Entry e : Tokenizer.finaldocumentandPositionSet.entrySet()) {
			HashMap<String, List<String>> termsindoc = (HashMap<String, List<String>>) e.getValue(); // Termid per document
			temp = new StringBuffer();
			int offset = termsindoc.size();
			temp.append(e.getKey().toString());
			temp.append("\t");
			temp.append(prevoffset);
			temp.append("\t");
			temp.append(offset);
			temp.append("\n");
			prevoffset = prevoffset + offset ;
			bw.write(temp.toString());
		}
		bw.close();
		//System.out.println(System.currentTimeMillis()/1000 - t1);
		System.out.println("Program ran sucessfully");
	}
	
}
