<%@ 
page import="java.nio.file.Path"
import = "java.nio.file.Paths"
import = "java.util.HashMap"
import ="java.util.Map"
import ="org.apache.lucene.analysis.standard.StandardAnalyzer"
import ="org.apache.lucene.document.Document"
import ="org.apache.lucene.index.DirectoryReader"
import ="org.apache.lucene.search.IndexSearcher"
import ="org.apache.lucene.search.Query"
import ="org.apache.lucene.queryparser.classic.MultiFieldQueryParser"
import ="org.apache.lucene.search.ScoreDoc"
import= "org.apache.lucene.store.Directory"
import= "org.apache.lucene.store.FSDirectory"
import="org.apache.lucene.search.TopDocs"
import="org.apache.lucene.search.highlight.*"
import="java.io.*"
import=" org.json.simple.JSONObject"
import=" org.apache.lucene.document.Document"
import="org.apache.lucene.analysis.TokenStream"
%> 
<head>

<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
</head>


<body style="background-color:silver;">
	<nav class="navbar navbar-default">
	  <div class="container-fluid">
	  
	    <div class="navbar-header">
	      <a class="navbar-brand" href="#">Search</a>
	    </div>
	    
	  </div>
	</nav>
	<div class="container">
	
   		<form role="search" action="index.jsp" method="POST">
	       <div class="input-group input-group-lg col-md-6 col-md-offset-3">
			  <span class="input-group-addon" id="sizing-addon2"><span class="glyphicon glyphicon-search"></span></span>
			  <input type="text" class="form-control" placeholder="Search" aria-describedby="sizing-addon2" name="query">
			</div>
	    </form>
	    	    
		<% 
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
				boosts.put("name", 1.0f);

				MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[] {"name","tweet","title"}, analyzer, boosts);
			
				Query query = parser.parse(request.getParameter("query"));
			//	ScoreDoc[] hits = isearcher.search(query, null, 20).scoreDocs;
				TopDocs topdocs=isearcher.search(query,10);
				ScoreDoc[] hits=topdocs.scoreDocs;
				
			
				
				
		
				
				//freshness System.out.println(hits[1].score-0.1);
				for (int i = 0; i <  hits.length; i++){				
				SimpleHTMLFormatter formatter=new SimpleHTMLFormatter("<b><font color='red'>","</font></b>");
				Highlighter highlighter=new Highlighter(formatter, new QueryScorer(query));
				highlighter.setTextFragmenter(new SimpleFragmenter(400));
				
			

				%><div class="lol">
					<pre><% 
						Document hitDoc = isearcher.doc(hits[i].doc);
					String tweet=hitDoc.get("name");
					if(tweet!=null){
						TokenStream tokenstream=analyzer.tokenStream("hello", new StringReader(tweet));
						try {
							tweet=highlighter.getBestFragment(tokenstream, tweet);
						} catch (Exception e) {
							e.printStackTrace();
						} 
					}
						out.println("Tweet: " + hitDoc.get("tweet") + "\n");
			        
						out.print("By: " + hitDoc.get("name"));
						if (! hitDoc.get("title").equals("null")){
							out.print("\ttitle: " + hitDoc.get("title"));
						}
						out.println();
						out.print("created at: " + hitDoc.get("created_at")+"\n");
						out.println("latitude: " + hitDoc.get("latitude") + "\n");
						out.println("longitude: " + hitDoc.get("longitude") + "\n");
						out.println("relevent score: " + +hits[i].score + "\n");
						%>
					</pre></div>
					<%
					
				} 
				
				
				
			} catch(Exception e){
				e.printStackTrace();
			}
		%> 
	    
	</div>
</body>


