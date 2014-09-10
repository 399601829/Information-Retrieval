import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class Jelinek_Mercer_Smoothing {
	static double lamda = 0.2;
	public static LinkedHashMap<String, LinkedHashMap<String,Double>> final_scores_d = new LinkedHashMap<>();
	
	public static void calculate_Jelinek_Mercer_Smoothing(){
		// tf(d,i) -> i.getTermFreq() number of occurrences of term i in document d.
		//len(d) -> we have it -> number of terms in the document
		LinkedHashMap<String,Double> inner_final_score = new LinkedHashMap<>();
		
		double summation_len_d = 0.0;
		for (Entry doc_e : Query.len_d.entrySet()) {
			summation_len_d = summation_len_d + Integer.parseInt(doc_e.getValue().toString());
		}
		
		//System.out.println(summation_len_d);
		for (Entry query_e : Query.querymap.entrySet()) {
			LinkedHashMap<String, Info> innerquerymaptp = (LinkedHashMap<String, Info>) query_e.getValue();
			for (Entry doc_e : Query.documentmap.entrySet()) {
				LinkedHashMap<String,Info> innerdocmaptp = (LinkedHashMap<String, Info>) doc_e.getValue();
				double score = 0.0;
				for (Entry query_e1 : innerquerymaptp.entrySet()) {
					Info i = innerdocmaptp.get(query_e1.getKey()) == null ? null : innerdocmaptp.get(query_e1.getKey());
					double numerator = i == null ? 0.0 : (i.getTermFreq());
					double denominator = Query.len_d.get(doc_e.getKey());
					double term1 = numerator/denominator;
					double term2 = Query.term_info_term_corpus.get(query_e1.getKey())/summation_len_d;
					double p_d_i = lamda* term1 + (1 - lamda) * term2;
					
					score = score + Math.log(p_d_i)/Math.log(2);
				}
				inner_final_score.put(doc_e.getKey().toString(), score);
			}
			final_scores_d.put(query_e.getKey().toString(), inner_final_score);
			inner_final_score = new LinkedHashMap<>();
		}	
		for (Entry e : final_scores_d.entrySet()){
			inner_final_score = (LinkedHashMap<String, Double>) (e.getValue());
			inner_final_score = (LinkedHashMap<String, Double>) Query.sortByValues(inner_final_score);
			//System.out.println(inner_final_score.entrySet());
			final_scores_d.put(e.getKey().toString(), inner_final_score);
		}
		//System.out.println(final_scores_d.entrySet());
	}
	
	public static void write_score_d() throws IOException{
		/*Writing termids to the file*/
		File file = new File("run.txt");
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		int rank = 0;
		LinkedHashMap<String, Double>inner_final_score = new LinkedHashMap<>();
		for (Entry e: final_scores_d.entrySet()){
			inner_final_score = (LinkedHashMap<String, Double>) e.getValue();
			for (Entry e1:inner_final_score.entrySet()){
				rank = rank+1;
				bw.write(e.getKey().toString()+"\t"+"0\t"+Query.docids.get(e1.getKey())+"\t\t"+rank +"\t"+e1.getValue()+"\t\trun1\n");
			}
			rank = 0;
		}
		bw.close();
	}

}
