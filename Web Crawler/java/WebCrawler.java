import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawler {
	
	static Queue robotList = new LinkedList<String>();


	// introduce 5 second delay
	// Merge similar calls

	/**
	 * Create a List of URL's that should not be crawled
	 * @throws IOException 
	 */
	public void createRobotstxtList(String robot_url) throws IOException{
		URL my_url = new URL(robot_url);
		HttpURLConnection httpcon = (HttpURLConnection) my_url.openConnection();
		/* Add User agent to get rid of 403 forbidden error */
		httpcon.setRequestProperty("User-Agent", "Mozilla/5.0");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(InputStream) httpcon.getContent()));
		String strTemp = "";
		while (null != (strTemp = br.readLine())) {
			if(strTemp.contains("Disallow:")){
				String parts[] = strTemp.split(":");
				if(!robotList.contains(parts[1].trim())){
					robotList.add(parts[1].trim());
				}
			}
		}
	}
	
	public String canonicalization(String url){
		
		//Removing all trailing "/" if they exist
		while (url.endsWith("/") || url.endsWith("#") || url.endsWith("?")){
				url = url.substring(0, url.length()-1);
		}
		
		//Removing index.html or index.htm if found at the end of any url
		String index = "index.html";
		if( url.endsWith("index.html")){
			url = url.substring(0, url.length()-index.length());
		}
		index = "index.htm";
		if( url.endsWith("index.htm")){
			url = url.substring(0, url.length()-index.length());
		}
		int secondindex = -1;
		//Removing duplicate slashes
		do{
		int firstindex =url.indexOf("//", 0);
		secondindex = url.indexOf("//", firstindex+1);
		
		if(secondindex!= -1){
			url = url.substring(0, secondindex) + url.substring(secondindex+1, url.length());
			
		}
		}while(secondindex!=-1);
		
		//Removing all trailing "/" if they exist
		while ( url.endsWith("/") || url.endsWith("#") || url.endsWith("?")){
				url = url.substring(0, url.length()-1);
		}
		
		url = url.toLowerCase();
		
		return url;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {

		String fullpage = ""; 
		String new_url = "http://www.ccs.neu.edu";
		Queue frontier = new LinkedList<String>();
		Queue visited_sites = new LinkedList<String>();
		frontier.add(new_url);
		WebCrawler w = new WebCrawler();
		LinkedHashSet<String> tp = new LinkedHashSet<>();
		LinkedHashMap<String, LinkedHashSet<String>> matrix = new LinkedHashMap<>();
		LinkedHashSet<String> matrix_children = new LinkedHashSet<>();
		HashMap<String, String> all_120_links = new HashMap<>();
		int hitcount = 0;
		
		/*Respecting robots.txt*/
		String robot_url = "http://www.ccs.neu.edu/robots.txt";
		w.createRobotstxtList(robot_url); // Creating a List of sites from robots.txt that should not be crawled for neu.edu domain
		// Also call for northeastern.edu domain
		robot_url = "http://www.northeastern.edu/robots.txt";
		w.createRobotstxtList(robot_url); 

		while(!frontier.isEmpty() && hitcount <120)    
		{
			
			/* Set seed to the ccs.neu.edu */
			URL my_url = new URL((String) frontier.peek());
			fullpage = ""; 
			
			if(!visited_sites.contains(my_url.toString()))
			{
				hitcount++;
				//put delay here
				System.out.println(my_url.toString());
				Thread.sleep(5000);
				HttpURLConnection httpcon = (HttpURLConnection) my_url.openConnection();
				boolean ctype = false;
				/* Add User agent to get rid of 403 forbidden error */
				httpcon.setRequestProperty("User-Agent", "Mozilla/5.0");
				
				/*Allowing only html and pdf sites*/
				if(httpcon.getContentType() != null){
					ctype = httpcon.getContentType()
							.contains("application/pdf")
							|| httpcon.getContentType().contains("text/html");
				}
				
				if(ctype && visited_sites.size()<100){
					visited_sites.add(frontier.peek());
					/* Create an appended version of the page in one string */
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(InputStream) httpcon.getContent()));
					String strTemp = "";
					while (null != (strTemp = br.readLine())) {
						fullpage = fullpage + strTemp;
					}
					/* End creating full page in string */
					
					/* Extracting links on the fullpage and adding links to frontier queue after canonicalization*/
					Document doc = Jsoup.parse(fullpage);
					Elements links = doc.select("a[href]");
					//print("\nLinks: (%d)", links.size());
					tp = new LinkedHashSet<>();
					matrix_children = null;
					matrix_children = new LinkedHashSet<>();
					for (Element link : links) {
						/* Add all links to queue regardless of content type */
						String pagelink = link.attr("abs:href").toString();
						//Canonicalize each url
						pagelink = w.canonicalization(pagelink);
						if(!visited_sites.contains(pagelink) && 
								!frontier.contains(pagelink) && 
								checkRobotstxtExclusion(pagelink) &&
								(pagelink.contains("neu.edu") || 
								pagelink.contains("northeastern.edu")) ){
							frontier.add(pagelink);
						}
						
						if(checkRobotstxtExclusion(pagelink) &&
								(pagelink.contains("neu.edu") || 
								pagelink.contains("northeastern.edu"))){
							matrix_children.add(pagelink);
						}
						
					}
					/* End -- Extracting links on the fullpage */
					//Connecting all outgoing links to the main URL
					matrix.put(my_url.toString(), matrix_children); // Matrix children can be null
					
				}
				if (ctype){
					all_120_links.put(frontier.peek().toString(), httpcon.getContentType());
				}
				
			}
			//Remove the link from frontier after processing
			frontier.remove();
		}
		System.out.println("===============================================================================================");
		int i=1,k=1;
		for (String key : matrix.keySet()) {
		    LinkedHashSet<String> templist = matrix.get(key);
		    LinkedHashSet<String> onetemplist = new LinkedHashSet<>();
		    for (String j : templist){
		    	if(all_120_links.containsKey(j)){
		    		onetemplist.add(j);
		    	}
		    }
		    matrix.put(key, onetemplist);
		}
		    
		try {
			String content = "";
			File file = new File("listoflinks.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			for (String key : matrix.keySet()) {
			    //System.out.println("Key = " + key + " - " + matrix.get(key));
				content = content + key+" ";
			    LinkedHashSet<String> templist = matrix.get(key);
			    for (Object j : templist){
			    	content = content + j+" ";
			    }
			    content = content + "\n";
			    
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkRobotstxtExclusion(String pagelink){
	
		for(Object o : robotList){
			if(pagelink.contains(o.toString()))
			{
				return false;
			}
		}
		return true;
	}
}
