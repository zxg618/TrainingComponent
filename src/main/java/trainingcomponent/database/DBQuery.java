package trainingcomponent.database;

import org.apache.jena.query.*;
import trainingcomponent.utility.StringUtil;

public class DBQuery {

	//http://blog.markwatson.com/2014/07/setting-up-your-own-sparql-endpoint-for.html
	private static final String DB_URL = "http://ec2-13-55-55-217.ap-southeast-2.compute.amazonaws.com:8890/sparql";
	private static final String NEW_LINE_CHAR = "\n";


	public static String findEntity(String entityName) {
		String result = "";

		//Note the format for Count!
		//https://www.w3.org/TR/sparql11-query/
		//
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT ?s (COUNT(*) as ?n) " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ "	?s ns:type.object.name '" + entityName + "'@en . " + NEW_LINE_CHAR
				+ "	?s ?p ?o . " + NEW_LINE_CHAR
				+ "} GROUP BY ?s ORDER BY DESC(?n)" + NEW_LINE_CHAR
				+ "LIMIT 1";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			result = sol.get("s").toString();
			System.out.println("findEntityByName: | " + entityName + " | "+result);
		}
		qexec.close();
		
		return result;
	}
	
	
	public static String getEntityName(String entityId) {
		String result = "";
	
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityId) + " ns:type.object.name ?o . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 10";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String o = sol.get("o").toString();
			System.out.println("getEntityName: | " + entityId + " | " + o);
			
			System.out.println("getEntityName removing @EN: | " + entityId + " | " + StringUtil.removeENLanguagePostfix(o));
			
		}
		qexec.close();
		
		return result;
	}
	
	/**
	 * 
	 * Excluding CVT relation.
	 * 
	 * @param entityId
	 * @return
	 */
	public static String findAllRelationAndObject(String entityId) {
		String result = "";

	
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityId) + " ?p ?o . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String p = sol.get("p").toString();
			String o = sol.get("o").toString();
			System.out.println("findAllRelationAndObject: | " + entityId + " | "+ p + " | " + o);
		}
		qexec.close();
		
		return result;
	}
	
	public static String findObject(String entityId, String relation){
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityId) + " " +  StringUtil.addNSBrackets(relation) + " ?o . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String o = sol.get("o").toString();
			System.out.println("findObject | " + entityId + " | "+ relation + " | " + o);
		}
		qexec.close();
		
		return "";
	}
	
	/**
	 * Between 2 entity ids
	 * 
	 * 
	 * findBinaryRelationById | http://rdf.freebase.com/ns/m.06w2sn5 | http://rdf.freebase.com/ns/people.person.place_of_birth | http://rdf.freebase.com/ns/m.0b1t1
	 * 
	 * 							Justin Bieber																					London, Canada
	 * 
	 * @param entityIdOne
	 * @param entityIdTwo
	 * @return
	 */
	public static String findBinaryRelationById(String entityIdOne, String entityIdTwo){
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityIdOne) + " ?p " +  StringUtil.addNSBrackets(entityIdTwo) + " . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String p = sol.get("p").toString();
			System.out.println("findBinaryRelationById|" + entityIdOne + " | "+ p + " | " + entityIdTwo);
		}
		qexec.close();
		
		return "";
	}
	
	
	/**
	 * Find the relation between an entity and a property value.
	 * 
	 */
	public static String findUnaryRelationbyProperty(String entityOneId, String property){
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityOneId) + " ?p '" + property + "'@en . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 100";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();

		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String p = sol.get("p").toString();
			System.out.println("findUnaryRelationbyProperty|" + entityOneId + "|" + p + "|" + property);
		}
		qexec.close();

		return "";
	}
	
	
	/**
	 * Between entityOne's ID and entityTwo's relation value
	 * 
	 * or
	 * Find relation between an entity and a given answer.
	 * 
	 * e.g.
	 * 
	 * findBinaryRelationByValue | http://rdf.freebase.com/ns/m.06w2sn5 | http://rdf.freebase.com/ns/people.person.gender | http://rdf.freebase.com/ns/m.05zppz | http://rdf.freebase.com/ns/type.object.name | Male
	 *								Justin Bieber																				Entity Male 																	  "Male"@en
	 * 
	 * @param entityOneId
	 * @param entityTwoValue
	 * @return
	 */
	public static String findBinaryRelationByValue(String entityOneId, String entityTwoValue){
		
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityOneId) + " ?p ?o . " + NEW_LINE_CHAR
				+ "?o ?p2 '" + entityTwoValue + "'@en . " + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String p = sol.get("p").toString();
			String o = sol.get("o").toString();
			String p2 = sol.get("p2").toString();
			System.out.println("findBinaryRelationByValue|" + entityOneId + "|" + p + "|" + o + "|" + p2 + "|"
					+ entityTwoValue);
		}
		qexec.close();
		
		return "";
	}
	
	
	
	
	/**
	 * Most types have names. The main exceptions are the so-called "mediator"
	 * types or "compound value types" (ie CVTs). CVTs typically have no name
	 * (ie the /type/object/name property has a null value). If there only two
	 * non-primitive named types connected to the CVT (ie the others are things
	 * like dates, currencies, etc), you may be able to distinguish the "best"
	 * name by the direction that you're coming from (ie it's the other named
	 * type). But for something like your /film/performance example, who says
	 * that the "best" property is /film/performance/film rather than
	 * /film/performance/character? Or, alternatively, if I started at the film,
	 * why wouldn't it be /film/performance/actor?
	 * 
	 * That's the whole point of mediators/CVTs. They sit between/among all the
	 * other nodes. You might be able to codify an algorithm to deal with the
	 * simple case (ie two linked named non-primitive types), but the general
	 * case is hard.
	 * 
	 * https://stackoverflow.com/questions/27475059/in-a-generic-manner-how-to-find-the-freebase-property-that-holds-a-user-readabl
	 * 
	 * Example data in Freebase:
	 * 
	 * m.014zcr	(George Clooney)	http://rdf.freebase.com/ns/base.popstra.celebrity.friendship		m.06505k9 (CVT)
	 * m.0169dl (Matt Damon)		http://rdf.freebase.com/ns/base.popstra.celebrity.friendship		m.06505k9 (CVT)
	 * 
	 *  
	 * m.06505k9 (CVT)	http://rdf.freebase.com/ns/base.popstra.friendship.participant	m.014zcr(George Clooney)
	 * m.06505k9 (CVT)	http://rdf.freebase.com/ns/base.popstra.friendship.participant	m.0169dl (Matt Damon)
	 * 
	 * This method returns
	 * 		http://rdf.freebase.com/ns/base.popstra.celebrity.friendship
	 * 
	 * and maybe others CVT relations connecting the 2 entities.
	 * 		http://rdf.freebase.com/ns/award.award_nominee.award_nominations
	 * 
	 */
	public static String findCVTRelationById(String entityIdOne, String entityIdTwo){
		
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityIdOne) + " ?p ?o . " + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityIdTwo) + " ?p ?o . " + NEW_LINE_CHAR
				+ "FILTER NOT EXISTS { ?o ns:type.object.name ?oo}" + NEW_LINE_CHAR
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String p = sol.get("p").toString();
			String o = sol.get("o").toString();
			System.out.println("findCVTRelation | " + entityIdOne + " | "+ p + " | " + o + " | " + entityIdTwo);
		}
		qexec.close();
		
		return "";
	}
	
	public static String findCVTRelationByValue(String entityIdOne, String entityTwoValue){
		
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityIdOne) + " ?p ?o . " + NEW_LINE_CHAR
				+ "?s ?p ?o . " + NEW_LINE_CHAR
				+ "?o ?p2 ?s . " + NEW_LINE_CHAR
				+ "?s ?p3 '" + entityTwoValue + "'@en ." + NEW_LINE_CHAR
				+ "FILTER (?s != " + StringUtil.addNSBrackets(entityIdOne) + ") ." + NEW_LINE_CHAR		//Exclude EnityOne itself
				+ "FILTER NOT EXISTS { ?o ns:type.object.name ?oo} ." + NEW_LINE_CHAR					//CVT entity has no name
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
													// Entity One
			String p = sol.get("p").toString();		// CVT relation going into CVT entity
			String o = sol.get("o").toString();		// CVT entity
			String p2 = sol.get("p2").toString();	// CVT relation going out of CVT entity
			String s = sol.get("s").toString();		// Entity Two
			String p3 = sol.get("p3").toString();	// Entity Two to EntityTwoValue
			
			System.out.println("findCVTRelationByValue|" + entityIdOne + "|" + p + "|" + o + "|" + p2 + "|"
					+ s + "|" + p3 + "|" + entityTwoValue);
		}
		qexec.close();
		
		return "";
	}
		
	public static String findObjectViaCVTRelation(String entityId, String cvtRelation){
		String sparqlQueryString = "PREFIX ns: <http://rdf.freebase.com/ns/>" + NEW_LINE_CHAR
				+ "SELECT * " + NEW_LINE_CHAR
				+ "WHERE {"  + NEW_LINE_CHAR
				+ StringUtil.addNSBrackets(entityId) + " " +  StringUtil.addNSBrackets(cvtRelation) + " ?o . " + NEW_LINE_CHAR
				+ "?s " +  StringUtil.addNSBrackets(cvtRelation) + " ?o . " + NEW_LINE_CHAR
				+ "    Filter (?s != " + StringUtil.addNSBrackets(entityId) + ") ."		//Excluding the source entity itself
				+ "}" + NEW_LINE_CHAR
				+ "LIMIT 1000";
		
		Query query = QueryFactory.create(sparqlQueryString);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(DB_URL, query);
		ResultSet results = qexec.execSelect();
		
		while (results.hasNext()) {
			QuerySolution sol = results.nextSolution();
			String o = sol.get("o").toString();
			String s = sol.get("s").toString();
			System.out.println("findObjectViaCVTRelation | " + entityId + " | "+ cvtRelation + " | " + o + " | " + s);
		}
		qexec.close();
		
		return "";
	}
	
	
	public static void dbQueryTest(){
		String eid = DBQuery.findEntity("Justin Bieber");
//		DBQuery.findEntity("China");
//		
//		DBQuery.getEntityName(eid);
//		
//		DBQuery.findAllRelationAndObject(eid);
//		
//		DBQuery.findObject(eid, "http://rdf.freebase.com/ns/people.person.sibling_s");
//
//		DBQuery.findBinaryRelationById(eid, "http://rdf.freebase.com/ns/m.0b1t1");
//		
//		DBQuery.findCVTRelationById("http://rdf.freebase.com/ns/m.0169dl", "http://rdf.freebase.com/ns/m.014zcr");
//		
//		DBQuery.findObjectViaCVTRelation("http://rdf.freebase.com/ns/m.0169dl", "http://rdf.freebase.com/ns/base.popstra.celebrity.friendship");
//
//		DBQuery.findEntity("Nina Dobrev");
//		
//		DBQuery.findEntity("FC Internazionale Milano");
//		
//		DBQuery.findEntity("Justin Bieber");
		
		DBQuery.findBinaryRelationByValue("http://rdf.freebase.com/ns/m.06w2sn5", "Male");
		
		DBQuery.findUnaryRelationbyProperty(eid, "Justin Bieber");
		
		DBQuery.findCVTRelationByValue(eid, "Jaxon Bieber");
	}

}
