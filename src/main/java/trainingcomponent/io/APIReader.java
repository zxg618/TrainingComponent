package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.jayway.jsonpath.JsonPath;

import trainingcomponent.utility.NameSpaceConverter;
import trainingcomponent.utility.TypeRepository;

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
	
	public String getTypeFromApis(String noun) {
		ArrayList<String> typeList = new ArrayList<String>();
		int counter = 0;
		int i = 0;
		int tmpNum = 0;
		
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
	    		  typeList.add(type.replace("[", "").replace("]", "").replace("\"", ""));
	    		  counter++;
	    		  if (counter == 3) {
	    			  break;
	    		  }
		      }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//System.out.println("-----Entity name is " + noun);
		String[] typeStrings = typeList.toArray(new String[0]);
		String allTypes = StringUtils.join(typeStrings, ",").toLowerCase();
		//System.out.println("Google api returns " + allTypes);
		String[] typesArray = allTypes.split(",");
		
		String msType = this.getCategoryFromMSApi(noun).toLowerCase();
		
		//System.out.println("MS api returns " + msType);
		
		String[] msTypesArray = msType.split("\\|");
		
		Map<String, Integer> mixTypes = new HashMap<String, Integer>();
		if (allTypes.length() > 0) {
			for (i = 0; i < typesArray.length; i++) {
				if (typesArray[i].equals("thing")) {
					continue;
				}
				if (mixTypes.containsKey(typesArray[i])) {
					tmpNum = mixTypes.get(typesArray[i]);
					mixTypes.replace(typesArray[i], tmpNum + 1);
				} else {
					mixTypes.put(typesArray[i], 1);
				}
			}	
		}
		
		//remove ms type
		if (msType.length() > 0 || false) {
			for (i = 0; i < msTypesArray.length; i++) {
				if (msTypesArray[i].equals("thing")) {
					continue;
				}
				if (mixTypes.containsKey(msTypesArray[i])) {
					tmpNum = mixTypes.get(msTypesArray[i]);
					mixTypes.replace(msTypesArray[i], tmpNum + 1);
				} else {
					mixTypes.put(msTypesArray[i], 1);
				}
			}	
		}
		
		typeList.clear();
		ArrayList<String> typeList2 = new ArrayList<String>();
		Iterator<Entry<String, Integer>> it = mixTypes.entrySet().iterator();
		while (it.hasNext()) {
			 Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
			 if (pair.getValue() > 1) {
				 typeList.add(pair.getKey());
			 }
			 typeList2.add(pair.getKey());
			 //System.out.println(pair.getKey() + " appears " + pair.getValue() + " times");
		}
		
		if (typeList.size() > 0) {
			return StringUtils.join(typeList.toArray(new String[0]), "|");
		}
		
		return StringUtils.join(typeList2.toArray(new String[0]), "|");
	}
	
	public String getGoogleTypeById(String noun, String entityId) {
		String entityType = "";
		Set<String> typeSet = new HashSet<String>();
		int i = 0;
		
		if (noun.length() < 1 || entityId.length() < 1) {
			return "";
		}
		
		try {
			//System.out.println("Starting process [name, id] [" + noun + ", " + entityId + "]");
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
		    	  String rawEntityId = JsonPath.read(element, "$.result.@id").toString();
	    		  String rawType = JsonPath.read(element, "$.result.@type").toString().replace("[", "").replace("]", "").replace("\"", "");
	    		  String[] idSubstrings = rawEntityId.split("/");
	    		  String tmpEntityId = idSubstrings[1] + "." + idSubstrings[2];
	    		  String[] typeArray = rawType.split(",");
	    		  for (i = 0; i < typeArray.length; i++) {
	    			  if (typeArray[i].equalsIgnoreCase("thing")) {
	    				  continue;
	    			  }
	    			  typeSet.add(typeArray[i]);
	    		  }
	    		  if (entityId.equalsIgnoreCase(tmpEntityId)) {
	    			  String types = StringUtils.join(typeSet.toArray(), "|");
	    			  entityType = types;
	    			  break;
	    		  }
		      }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return entityType;
	}
	
	/**
	 * 
	 * returns null if no entity is found
	 * 
	 * @param entityId
	 * @return
	 */
	public GKGEntity getEntity(String entityId){
		GKGEntity entity = null;
		
		try {
		      HttpTransport httpTransport = new NetHttpTransport();
		      HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
		      JSONParser parser = new JSONParser();
		      GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
		      url.put(API_PARAM_IDS, NameSpaceConverter.noNSToGKGQueryId(entityId));
//		      url.put(API_PARAM_QUERY, noun);
		      url.put(API_PARAM_LIMIT, "5");
		      url.put(API_PARAM_IDENT, "true");
		      url.put(API_PARAM_KEY, API_KEY);
		      HttpRequest request = requestFactory.buildGetRequest(url);
		      HttpResponse httpResponse = request.execute();
		      JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
		      JSONArray elements = (JSONArray) response.get("itemListElement");

		      //only expecting one item
		      for (Object element : elements) {
		    	  entity = retrieveEnity(element);
		      }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return entity;
	}
	
	private GKGEntity retrieveEnity(Object jsonObject) {
		GKGEntity entity = new GKGEntity();
		TypeRepository typeRepo = TypeRepository.getInstance();

		String scoreString = JsonPath.read(jsonObject, "$.resultScore").toString();
		double score = Double.parseDouble(scoreString);

		String idString = JsonPath.read(jsonObject, "$.result.@id").toString();
		 
		JSONArray types = (JSONArray) JsonPath.read(jsonObject, "$.result.@type");

		String name = "";
		try {
			name = JsonPath.read(jsonObject, "$.result.name").toString();			// /m/05890x6	has no name
		} catch (com.jayway.jsonpath.PathNotFoundException pnfe) {
			name = "";
		}

		String description = "";
		try {
			description = JsonPath.read(jsonObject, "$.result.description").toString();
		} catch (com.jayway.jsonpath.PathNotFoundException pnfe) {
			description = "";
		}

		//System.out.println(idString + "|" + name + "|" + types + "|" + description);

		if (Double.compare(score, 0) > 0 && idString.startsWith("kg:/m")) { // only taking Freebase mid
			entity.setEntityId(NameSpaceConverter.GKGFullIdToNoNS(idString));
			entity.setEntityName(name);
			entity.setDescription(description);
			for (Object typeObject : types) {
				String typeStr = typeObject.toString();
				String typeCode = typeRepo.getTypeCode(typeStr);

				if (!typeStr.equals("Thing") && typeCode != null) {										// Exclude root type Thing
					TypeRecord typeRecord = new TypeRecord();
					typeRecord.setType(typeStr);
					typeRecord.setTypeCodeStr(typeCode);
					entity.addType(typeRecord);
				}
			}
			entity.setTypes(simplifyTypes(entity.getTypes()));
			
		}

		return entity;
	}
	
	private Set<TypeRecord> simplifyTypes(Set<TypeRecord> types){
		Set<TypeRecord> simplefiedTypes = new HashSet<>();
		
		if(types.size() <= 1 ){
			return types;
		}

		
		for(TypeRecord type: types){
			String typeCodeBasic = removeTrailingZeros(type.getTypeCodeStr());
			
			boolean isFound = false;
			for(TypeRecord type2: types){
				if(type2.equals(type)){
					continue;								//no need to compare with itself
				}
				
//System.out.println("=="+type2.getTypeCodeStr()+"|"+typeCodeBasic);				
				if(type2.getTypeCodeStr().startsWith(typeCodeBasic)){
//System.out.println("--"+type2.getTypeCodeStr()+"|"+typeCodeBasic);				
					isFound = true;
					break;
				}
			}
			
			if(isFound == false){
				simplefiedTypes.add(type);	
			}
		}
		
		return simplefiedTypes;
	}
	
	private String removeTrailingZeros(String numberIn){
		String result = "";
		
		int i = numberIn.length() - 1;
		for(; i >= 0; i--){
			if(numberIn.charAt(i) != '0'){
				break;
			}
		}
		
		result = numberIn.substring(0, i+1);
		
		return result;
	}
}
