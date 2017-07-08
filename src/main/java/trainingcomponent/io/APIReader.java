package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang3.StringUtils;
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
	    		  String type = JsonPath.read(element, "$.result.@type").toString();
		    	  if ((counter == 0 || Double.compare(tmpScore, score) > 0) && type.indexOf("Event") < 0) {
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
	
	public String getTopThreeIdsByNoun(String noun) {
		ArrayList<String> idList = new ArrayList<String>();
		int counter = 0;
		
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
		      
		      for (Object element : elements) {
	    		  String type = JsonPath.read(element, "$.result.@type").toString();
	    		  String idString = this.format(JsonPath.read(element, "$.result.@id").toString());
	    		  if (type.indexOf("Event") < 0 && idString.charAt(0) == 'm') {
	    			  idList.add(idString);
	    			  counter++;
	    		  }
	    		  
	    		  if (counter == 3) {
	    			  break;
	    		  }
		      }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String[] idStrings = idList.toArray(new String[0]);
		return StringUtils.join(idStrings, ",");
	}
	
	public String getCategoryFromMSApi(String noun) {
		ArrayList<String> catList = new ArrayList<String>();
		String result = "";
		try {
		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      //JSONParser parser = new JSONParser();
		      String urlString = HOST + PCE_API;
		      GenericUrl url = new GenericUrl(urlString);
		      url.put(PARA_INSTANCE, noun);
		      url.put(PARA_TOPK, TOPK_VALUE);
		      HttpRequest request = requestFactory.buildGetRequest(url);
		      HttpResponse httpResponse = request.execute();
		      
		      InputStream in = httpResponse.getContent();
		      BufferedReader br = new BufferedReader(new InputStreamReader(in));
		      String line = "";
		      if ((line = br.readLine()) != null) {
		    	  result = line.replace("\"", "").replace("{", "").replace("}", "");
		    	  String[] catRatings = result.split(",");
		    	  for (int i = 0; i < catRatings.length; i++) {
		    		  String cat = catRatings[i].split(":")[0];
		    		  catList.add(cat);
		    	  }
		      }
		      
//		      JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
//		      for ( Object key : response.keySet() ) {
//		    	    catList.add(key.toString());
//		      }
		      
		      //System.out.println(noun + " " + response.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return StringUtils.join(catList.toArray(new String[0]), "|");
	}
	
	protected String format(String input) {
		return input.replace("/", ".").replace("kg:.", "");
	}
}
