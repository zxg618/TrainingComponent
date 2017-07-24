package trainingcomponent.utility;

public class NameSpaceConverter {

	public static String autoModify(String entityURL){
		if (entityURL != null && !"".equals(entityURL)) {
			if(entityURL.startsWith("http://rdf.freebase.com/ns/")){		//web q templates
				entityURL = freebasePlusBrackets(entityURL);
			} else {
				entityURL = noNSToFreebaseDB(entityURL);					//fb tempaltes
			}
		}
		
		return entityURL;
	}
	
	public static String facebookToNoNS(String facebookEntityURL){
		String result = "";
		
		if (facebookEntityURL != null && !"".equals(facebookEntityURL)) {

			// www.freebase.com/m/0cqt90
			// => m.0cqt90

			result = facebookEntityURL.replace("www.freebase.com/", "");
			result = result.replace("/", ".");
		}
		
		return result;
	}
	
	
	public static String freebaseToNoNS(String freebaseEntityURL){
		String result = "";
		
		if (freebaseEntityURL != null && !"".equals(freebaseEntityURL)) {

			// http://rdf.freebase.com/ns/m.06w2sn5
			// => m.06w2sn5

			result = freebaseEntityURL.replace("http://rdf.freebase.com/ns/", "");
		}
		
		return result;
	}
	
	public static String noNSToFreebaseDB(String entityURL){
		String result = "";
		
		if (entityURL != null && !"".equals(entityURL)) {

			// m.06w2sn5
			// => <http://rdf.freebase.com/ns/m.06w2sn5>

			result = "<http://rdf.freebase.com/ns/" + entityURL + ">";
		}
		
		return result;
	}
	
	public static String freebasePlusBrackets(String entityURL){
		String result = "";
		
		if (entityURL != null && !"".equals(entityURL)) {

			// http://rdf.freebase.com/ns/m.06w2sn5
			// => <http://rdf.freebase.com/ns/m.06w2sn5>

			result = "<" + entityURL + ">";
		}
		
		return result;
	}
	
	public static boolean hasFreebaseNS(String entityURL) {
		boolean result = false;

		if (entityURL != null && !"".equals(entityURL)) {
			if(entityURL.startsWith("http://rdf.freebase.com/ns/")){
				result = true;
			}
		}

		return result;
	}
	
	public static String noNSToGKGQueryId(String entityId){
		String result = "";
		
		if (entityId != null && !"".equals(entityId)) {

			// m.02mjmr
			// => /m/02mjmr

			result = "/" + entityId.replace(".", "/");
		}
		
		return result;
	}
	
	public static String GKGFullIdToNoNS(String input) {
		// kg:/m/02mjmr
		// =>
		// m.02mjmr
		
		return input.replace("/", ".").replace("kg:.", "");
	}
}
