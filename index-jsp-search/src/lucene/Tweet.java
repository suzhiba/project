package lucene;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Tweet {
	public String name;
	public String title;
	public String text;
	public double [] location;
	public String created_at;
	
	public Tweet(String name, String title, String text,  double []loc, String c) {
		this.name = name;
		this.title = title;
		this.text = text;
		this.created_at = c;

		this.location = new double[2];
		for(int i = 0; i < loc.length; i++) {
			this.location[i] = loc[i];
		}
		
	}
}
