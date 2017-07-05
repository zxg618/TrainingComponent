package trainingcomponent.utility;

public class StringUtil {
		private static final String FREEBASE_NAMESPACE = "http://rdf.freebase.com/ns/";
		
		/**
		 * Remove "http://rdf.freebase.com/ns/"
		 * 
		 * http://rdf.freebase.com/ns/m.06w2sn5 => m.06w2sn5
		 * 
		 * @param entityId
		 * @return
		 */
		public static String removeNamesapcePrefix(String entityId) {
			
			if (entityId.trim().startsWith(FREEBASE_NAMESPACE)) {
				entityId = entityId.substring(FREEBASE_NAMESPACE.length(), entityId.length());
			}
			
			return entityId;
		}
		
		
		/**
		 * Justin Bieber@en => Justin Bieber
		 * 
		 * @return
		 */
		public static String removeENLanguagePostfix(String entityValue){
			if(entityValue.trim().endsWith("@en")){
				entityValue = entityValue.substring(0, entityValue.length() - "@en".length());
			}
			
			return entityValue;
		}
		
		/**
		 * 
		 * http://rdf.freebase.com/ns/m.06w2sn5 => <http://rdf.freebase.com/ns/m.06w2sn5>
		 * 
		 */
		public static String addNSBrackets(String entityId){
			
			if (!entityId.trim().startsWith("<")) {
				entityId = "<" + entityId + ">";
			}
			
			return entityId;
		}
		
		/**
		 * 
		 * @param str
		 * @return
		 */
		public static String capitalizeFirstLetter(String str) {
			String result;
			result = str.substring(0, 1).toUpperCase() + str.substring(1);
			
			return result;
		}	
		
		
		
	

}
