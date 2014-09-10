import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;


public class OkapiTF {
	public static LinkedHashMap<String, LinkedHashMap<String,Double>> final_scores_d = new LinkedHashMap<>();
	//(query_id -> (doc_id, score))

	public static void calculateOkapiTFScore(){
		double square_di_sum = 0;
		double square_qi_sum = 0;
		LinkedHashMap<String,Double> inner_final_score = new LinkedHashMap<>();
		for (Entry query_e : Query.querymap.entrySet()){

			LinkedHashMap<String,Info> innerquerymaptp = (LinkedHashMap<String, Info>) query_e.getValue();
			for(Entry query_e1 : innerquerymaptp.entrySet()){
				Info i = (Info) query_e1.getValue();
				square_qi_sum = square_qi_sum + i.getOktf()*i.getOktf();
			}
			double norm_qi = Math.sqrt(square_qi_sum);
			double norm_di = 0;
			for (Entry doc_e : Query.documentmap.entrySet()){
				LinkedHashMap<String,Info> innerdocmaptp = (LinkedHashMap<String, Info>) doc_e.getValue();
				for(Entry doc_e1 : innerdocmaptp.entrySet()){
					Info i = (Info) doc_e1.getValue();
					square_di_sum = square_di_sum + i.getOktf()*i.getOktf();
				}
				norm_di = Math.sqrt(square_di_sum);
				
				//Take dot product
				double numerator = 0.0;
				for(Entry doc_e1 : innerdocmaptp.entrySet()){
					if (null!=innerquerymaptp.get(doc_e1.getKey())){
						Info qi = (Info) doc_e1.getValue();
						Info di = innerquerymaptp.get(doc_e1.getKey());
						numerator = numerator + qi.getOktf() * di.getOktf(); 
					}
				}
				double score_d = numerator / (norm_di*norm_qi);
				//System.out.println(query_e.getKey()+" "+doc_e.getKey()+" "+score_d);
				inner_final_score.put(doc_e.getKey().toString(), score_d);
				square_di_sum = 0;
			}
			final_scores_d.put(query_e.getKey().toString(), inner_final_score);
			inner_final_score = new LinkedHashMap<>();
			square_qi_sum = 0;
		}
		
		
		for (Entry e : final_scores_d.entrySet()){
			inner_final_score = (LinkedHashMap<String, Double>) (e.getValue());
			inner_final_score = (LinkedHashMap<String, Double>) Query.sortByValues(inner_final_score);
			//System.out.println(inner_final_score.entrySet());
			final_scores_d.put(e.getKey().toString(), inner_final_score);
		}
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
