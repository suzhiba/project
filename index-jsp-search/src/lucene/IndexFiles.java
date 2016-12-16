package lucene;


import java.io.FileNotFoundException;


import lucene.Tweet;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;




public class IndexFiles {
	
	
	
	public static void main(String[] args)throws ParseException{
		
	 	try {
	 		//File folder = new File("C:/Users/29563/Desktop/project/data");
	 		File folder = new File("./data");
		 	
	 		File[] fileList = folder.listFiles();
			Path path = Paths.get("./index");
			Directory dir = FSDirectory.open(path);
			StandardAnalyzer analyzer = new StandardAnalyzer();

		    System.out.println("g");
			 //	InputStream is = new FileInputStream("C:/Users/29563/Desktop/lucene&web/data/" + fileList[i].getName());
			   	
	 		for (int i = 0; i < fileList.length; ++i) {
	 			System.out.println(i);

	 			System.out.println(fileList[i].getName());
	 	
			   	InputStream is = new FileInputStream("./data/" + fileList[i].getName());
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				IndexWriterConfig configure = new IndexWriterConfig(analyzer);
				IndexWriter iwriter = new IndexWriter(dir, configure);
			    String line;
				while((line = br.readLine()) != null)
				{   
					
				
				 	JSONParser parser = new JSONParser();
					Object obj = parser.parse(line.toString());
					JSONObject jsonObject = (JSONObject) obj;
					
					String name = (String) jsonObject.get("name");
			    String title = (String) jsonObject.get("title");
					String text = (String) jsonObject.get("text");
					String created = (String) jsonObject.get("created_at");
				
					JSONArray reducedLoc = (JSONArray) jsonObject.get("location");
					JSONArray locArray = (JSONArray) reducedLoc.get(0);
					double []loc = new double[2];
					loc = GetPoint(locArray);
					Tweet tweet = new Tweet(name,title, text, loc, created);
						index(tweet,iwriter);
					
	
					
					
				}
				br.close();
				isr.close();
				is.close();
				  iwriter.close();
				}
				
			
			  	
		    
	 		
		} catch (FileNotFoundException e) {
			  	e.printStackTrace();
		} catch (IOException e) {
		  		e.printStackTrace();
		}
	
	}
	

	public static void index(Tweet tweet, IndexWriter iwriter) {
		try{
		    Document doc = new Document();
		    doc.add(new Field("name", tweet.name, TextField.TYPE_STORED));
		    doc.add(new Field("tweet", tweet.text, TextField.TYPE_STORED ));
		    
		    doc.add(new Field("created_at", tweet.created_at, TextField.TYPE_STORED ));
		  if (tweet.title != null){
			    doc.add(new Field("title", tweet.title, TextField.TYPE_STORED ));
		    }else {
		    	doc.add(new Field("title", "null", TextField.TYPE_STORED ));
		    }
		    
		    doc.add(new DoubleField("longitude", tweet.location[0], DoubleField.TYPE_STORED));
		    doc.add(new DoubleField("latitude", tweet.location[1], DoubleField.TYPE_STORED));
		    
		    iwriter.addDocument(doc);

		} catch (Exception ex){
		  	ex.printStackTrace();;
		}
	}
	
	public static double[] GetPoint(JSONArray arr) {
		JSONArray first = (JSONArray) arr.get(0);
		JSONArray second = (JSONArray) arr.get(1);
		JSONArray third = (JSONArray) arr.get(2);
		double ay = (double) first.get(1);
		double bx = (double) second.get(0);
		double by = (double) second.get(1);
		double cx = (double) third.get(0);
		double []location = new double[2];
		double lon = (bx + cx)/2.0;
		double lat = (ay + by)/2.0;
		location[0] = lon;
		location[1] = lat;
		return location;
	}
}

