package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.jayway.jsonpath.JsonPath;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class APIReader {
	protected String api = "";
	
	public APIReader() {
		this.api = KGS_API;
	}
	
	public String getIdByNoun(String noun) {
		String idString = "";
		
		try {

		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      JSONParser parser = new JSONParser();
		      GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
		      url.put(API_PARAM_QUERY, noun);
		      url.put(API_PARAM_LIMIT, "10");
		      url.put(API_PARAM_IDENT, "true");
		      url.put(API_PARAM_KEY, API_KEY);
		      HttpRequest request = requestFactory.buildGetRequest(url);
		      HttpResponse httpResponse = request.execute();
		      JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
		      JSONArray elements = (JSONArray) response.get("itemListElement");
		      
		      int counter = 0;
		      double score = 0;
		      
		      
		      for (Object element : elements) {
		    	  String scoreString = JsonPath.read(element, "$.resultScore").toString();
	    		  double tmpScore = Double.parseDouble(scoreString);
		    	  if (counter == 0 || Double.compare(tmpScore, score) > 0) {
		    		  score = tmpScore;
		    		  idString = JsonPath.read(element, "$.result.@id").toString();
		    	  }
//		    	  System.out.println("---------" + counter + "-------------");
		        //System.out.println(JsonPath.read(element, "$.result.name").toString());
//		    	  System.out.println(JsonPath.read(element, "$.resultScore").toString());
//		    	  System.out.println(JsonPath.read(element, "$.result.@id").toString());
		        counter++;
		      }
		      
		      
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return this.format(idString);
	}
	
	protected String format(String input) {
		return input.replace("/", ".").replace("kg:.", "");
	}
}
