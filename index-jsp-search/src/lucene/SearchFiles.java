package lucene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.json.simple.JSONObject;

public class SearchFiles {
	
	public static void main(String[] args){
		try{
			
			Path path = Paths.get("./index");
			Directory dir = FSDirectory.open(path);
			DirectoryReader ireader = DirectoryReader.open(dir);
			IndexSearcher isearcher = new IndexSearcher(ireader);
			StandardAnalyzer analyzer = new StandardAnalyzer();
			//get each token
			Map<String, Float> boosts = new HashMap<String, Float>();
		    boosts.put("text", 4.0f);
		    boosts.put("title", 1.5f);//term 
		    /*boosts.put("htag", 6.0f);*/
			boosts.put("name", 0.4f);
			/*
			 * method2
			String keyWord="hi";
			Term term=new Term("text",keyWord);
			TermQuery query=new TermQuery(term);
			TopDocs topdocs=isearcher.search(query,10);
			ScoreDoc[] scoredocs=topdocs.scoreDocs;
			System.out.println("查询结果总数:" + topdocs.totalHits);
			System.out.println("最大的评分:"+topdocs.getMaxScore());
			for(int i=0;i<scoredocs.length;i++){
				int doc=scoredocs[i].doc;
				Document document=isearcher.doc(doc);
				System.out.println("====================文件【"+(i+1)+"】=================");
				System.out.println("检索关键词："+term.toString());
				
				System.out.println("文件路径:"+document.get("path"));
				System.out.println("文件ID:"+scoredocs[i].doc);
				String name=document.get("name");
				String text=document.get("text");
		
				SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
				Highlighter highlighter=new Highlighter(formatter, new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(100));
			
				if(name!=null){
					TokenStream tokenstream=analyzer.tokenStream(keyWord, new StringReader(text));
					try {
						name=highlighter.getBestFragment(tokenstream, name);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
		
				System.out.println("文件内容:"+name);
				System.out.println("文件内容:"+text);
				System.out.println("匹配相关度："+scoredocs[i].score);
			}
		*/   
			
			MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"name","tweet","title"}, analyzer, boosts);
		
			Query query = parser.parse("b");
		//	ScoreDoc[] hits = isearcher.search(query, null, 20).scoreDocs;
			TopDocs topdocs=isearcher.search(query,20);
			ScoreDoc[] hits=topdocs.scoreDocs;
			
		
			

			
			//freshness System.out.println(hits[1].score-0.1);
			for (int i = 0; i <  hits.length; i++){				
			SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
			Highlighter highlighter=new Highlighter(formatter, new QueryScorer(query));
			highlighter.setTextFragmenter(new SimpleFragmenter(400));
				Document hitDoc = isearcher.doc(hits[i].doc);
			/*	String value=hitDoc.get("text");
				String str="";
	            if (value != null) {    
	                TokenStream tokenStream = analyzer.tokenStream("text", new StringReader(value));    
	                String str1 = highlighter.getBestFragment(tokenStream, value);    
	                str=str+str1;    
	            }     */
				String tweet=hitDoc.get("tweet");
				if(tweet!=null){
					TokenStream tokenstream=analyzer.tokenStream("hello", new StringReader(tweet));
					try {
						tweet=highlighter.getBestFragment(tokenstream, tweet);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				String name=hitDoc.get("name");
				
				
				String latitude=hitDoc.get("latitude");
				String longitude=hitDoc.get("longitude");
				//String title=hitDoc.get("title");
				System.out.println("fileID:"+hits[i].doc);
				System.out.println("Name: " + hitDoc.get("name"));
				System.out.println("title: " + hitDoc.get("title"));
				System.out.println("tweet: " + hitDoc.get("tweet"));
				System.out.println("created_at: " + hitDoc.get("created_at"));
				System.out.println("latitude: " + hitDoc.get("latitude"));
				System.out.println("longitude: " + hitDoc.get("longitude"));
				System.out.println("relevent score:"+hits[i].score);
				OutputStream is = new FileOutputStream("C:/Users/29563/Desktop/project/qresult.txt",true);
			   	OutputStreamWriter isr = new OutputStreamWriter(is);
			   	BufferedWriter br = new BufferedWriter(isr);
			
				JSONObject obj = new JSONObject();
				obj.put("name", name);
				obj.put("tweet", tweet);
				obj.put("latitude", latitude);
				obj.put("longitude", longitude);
				 br.write(obj.toJSONString());
				 br.write(",");
	
				 
				 br.close();
				System.out.println();
				System.out.println();

			//}
			
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
}