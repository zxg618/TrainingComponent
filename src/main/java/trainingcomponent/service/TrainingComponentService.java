package trainingcomponent.service;
import static trainingcomponent.constant.Constant.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import trainingcomponent.database.DBQuery;
import trainingcomponent.io.APIReader;
import trainingcomponent.io.FileReader;
import trainingcomponent.io.GKGEntity;
import trainingcomponent.io.TypeRecord;
import trainingcomponent.utility.WekaClassifier;

public class TrainingComponentService {
	
	public void getAllIds() {
		//this.getAllNamesFromFile();
		this.verifyCurrentIds();
	}
	
	public void testQuery() {
		FileReader fr = new FileReader();
		
		String[] entityIds = fr.readEntityIdFile();
		String[] answers = fr.readQuestionTrainFile();
		int total = entityIds.length;
		int i = 0;
		
		if (total != answers.length) {
			System.out.println("Training files length does NOT match.");
		} else {
			System.out.println("There are total " + total + " trining data.");
		}
		
		for (i = START; i < total; i++) {
			if (i == 1499) {
				continue;
			}
			this.runQueriesForOneData(entityIds[i], answers[i], i + 1);
		}
	}
	
	public void verifyFirstIdOfEachEntity() {
		FileReader fr = new FileReader();
		fr.searchFirstIdAppearance();
	}
	
	protected void runQueriesForOneData(String entityIdString, String answerString, int index) {
		String[] idList = this.getAllIdsFromCSVString(entityIdString);
		String question = answerString.split("\t")[0];
		String answers = answerString.split("\t")[1];
		String[] answerSubStrings = answers.split("\\|");
		String entityName = entityIdString.split("\t")[0];
		//entityName = entityName.replace("\\", "\\\\");
		entityName = StringEscapeUtils.unescapeJava(entityName);
		int idLength = idList.length;
		int answerLength = answerSubStrings.length;
		int i = 0;
		int j = 0;
		boolean printFlag = true;
		Map<String, Integer> relationMap = new HashMap<String, Integer>();
		
		
		for (i = 0; i < answerLength; i++) {
			for (j = 0; j < idLength; j++) {
				String line = "";
				int outputLength = 0;
				line += index + "\t";
				line += question + "\t";
				line += entityName + "\t";
				String eid = PREFIX + idList[j];
				line += eid + "\t";
				String answer = answerSubStrings[i];
				if (answer.indexOf("\'") >= 0) {
					answer = answer.replace("\'", "\\'");
				}
				String relation = "";
				//System.out.println("----------start querying------------");
				relation += DBQuery.findUnaryRelationbyProperty(eid, answer);
				outputLength = relation.length();
				//System.out.println("----------unary done------------");
				String binaryOutput = DBQuery.findBinaryRelationByValue(eid, answer);
				if (outputLength > 0 && binaryOutput.length() > 0) {
					relation += ",";	
				}
				relation += binaryOutput;
				//System.out.println("---------binary done-------------");
				String cvtOutput = DBQuery.findCVTRelationByValue(eid, answer);
				if (relation.length() > 0 && cvtOutput.length() > 0) {
					relation += ",";
				}
				relation += cvtOutput;
				//System.out.println("--------cvt done--------------");
				if (relation.length() < 5 || entityName.equalsIgnoreCase(answers)) {
					printFlag = false;
				} else {
					line += relation + "\t";
				}
				
				if (answer.indexOf("\\'") >= 0) {
					answer = answer.replace("\\'", "\'");
				}
				line += answers;
				if (printFlag) {
					//System.out.println("-------" + line);
					//System.out.println(line);
					if (relationMap.containsKey(line)) {
						int count = relationMap.get(line);
						count++;
						relationMap.replace(line, count);
					} else {
						relationMap.put(line, 1);
					}
					break;
				} else {
					//test purpose
					//System.out.print("BAD");
					//System.out.println(line);
					printFlag = true;
				}
			}
		}
		
		Iterator<Entry<String, Integer>> it = relationMap.entrySet().iterator();
		int maxCount = 0;
		String relation = "";
		while (it.hasNext()) {
			Entry<String, Integer> pair = it.next();
			int tmpCount = pair.getValue();
			if (tmpCount > maxCount) {
				maxCount = tmpCount;
				relation = pair.getKey();
			}
		}
		
		if (maxCount != 0) {
			System.out.println(maxCount + "++++++++" + relation);	
		}
		
		
	}
	
	protected String[] getAllIdsFromCSVString(String input) {
		ArrayList<String> idList = new ArrayList<String>();
		String[] subStrings = input.split("\t");
		String[] subStrings2 = subStrings[1].split(",");
		int i = 0;
		
		for (i = 0; i < subStrings2.length; i++) {
			idList.add(subStrings2[i]);
		}
		
		if (!subStrings[2].equals("N/A")) {
			idList.add(subStrings[2]);
		}
		
		return idList.toArray(new String[0]);
	}
	
	public String[] getAllNamesFromFile() {
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		
		String[] nouns = fr.read();
		int length = nouns.length;
		int i = 0;
		ArrayList<String> idList = new ArrayList<String>();
		
		for (i = 0; i < length; i++) {
			String id = ar.getIdByNoun(nouns[i]);
			System.out.println(nouns[i] + "\t" + id);
			idList.add(id);
		}
		
		return idList.toArray(new String[0]);
	}

	
	protected void verifyCurrentIds() {
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		
		String[] nouns = fr.getWholeLine();
		int length = nouns.length;
		int i = 0;
		
		for (i = 0; i < length; i++) {
			String line = nouns[i];
			String[] substrings = line.split("\t");
			String idsString = ar.getTopThreeIdsByNoun(substrings[1]);
			
			if (substrings.length < 3) {
				System.out.println(substrings[1] + "\t" + idsString + "\tN/A");
			} else {
				String originalId = substrings[2].replace("http://rdf.freebase.com/ns/", "");
				System.out.print(substrings[1] + "\t" + idsString + "\t" + originalId);
				if (idsString.indexOf(originalId) < 0) {
					System.out.println("\t----------");
				} else {
					System.out.println("");
				}
			}
		}
	}
	
	protected String formatString(String input) {
		String newString = input;
		
		if (input.indexOf("http://www.freebase.com") >= 0) {
			String[] substrings = input.split(" ");
			newString = input.replace(substrings[0], "");
		}
		
		return newString;
	}
	
	public void findQuestionEntityCategory() {
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		
		//String filePath = DATA_PATH + FB_TEMP_INPUT_FILE;
		String filePath = DATA_PATH + WEBQ_TEMP_INPUT_FILE;
		String[] lines = fr.getAllLinesFromFile(filePath);
		int totalLines = lines.length;
		int i = 0;
		
		for (i = 0; i < totalLines; i++) {
			String[] subStrings = lines[i].split("\t");
			String qEntityId = subStrings[3];
			String entityName = DBQuery.getEntityName(PREFIX + qEntityId);
			String categories = ar.getTypeFromApis(entityName);
			//String categories = ar.getCategoryFromMSApi(entityName);
			if (categories.length() > 0) {
				System.out.println(lines[i] + "\t" + categories);	
			} else {
				System.out.println(lines[i]);
			}
			
		}
		
	}
	
	public void findQuestionEntityCategoryFromBothApis() {
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		
		String filePath = DATA_PATH + FB_TEMP_INPUT_FILE;
		//String filePath = DATA_PATH + WEBQ_TEMP_INPUT_FILE;
		String[] lines = fr.getAllLinesFromFile(filePath);
		int totalLines = lines.length;
		int i = 0;
		
		for (i = 0; i < totalLines; i++) {
			String[] subStrings = lines[i].split("\t");
			String qEntityId = subStrings[3];
			String entityName = DBQuery.getEntityName(PREFIX + qEntityId);
			String categories = ar.getTypeFromApis(entityName);
			if (categories.length() > 0) {
				System.out.println(lines[i] + "\t" + categories);	
			} else {
				System.out.println(lines[i]);
			}
			
		}
		
	}
	
	public void getAllTypesFromFBData() {
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		int i = 0;
		String entityName = "";
		String type = "";
		
		
		String filePath = DATA_PATH + FB_TEMP_INPUT_FILE_ALL;
		String[] lines = fr.getAllLinesFromFile(filePath);
		
		for (i = 0; i < lines.length; i++) {
			String[] subStrings = lines[i].split("\t");
			entityName = DBQuery.getEntityName(PREFIX + subStrings[3]);
			type = ar.getGoogleTypeById(entityName, subStrings[3]);
			System.out.println(lines[i] + "\t" + type);
		}
	}
	
	//final solution to get web q relations
	
	public void findWebqRelations() {
		FileReader fr = new FileReader();
		String filePath1 = INPUT_PATH + OUTPUT_ID_FILE_NAME;
		String filePath2 = DATA_PATH + QUESTION_TRAIN_FILE;
		//String filePath3 = DATA_PATH + WEBQ_TEMP_INPUT_FILE;
		
		String[] entityIdLines = fr.getAllLinesFromFile(filePath1);
		String[] trainLines = fr.getAllLinesFromFile(filePath2);
		//String[] webqTempateLines = fr.getAllLinesFromFile(filePath3);
		
		int i = 0;
		int j = 0;
		int k = 0;
		int totalLines = entityIdLines.length;
		
		for (i = 0; i < totalLines; i++) {
			if (i == 1498) {
				continue;
			}
			
			//System.out.println("Process line " + i + " .........");
			String[] entityIds = this.getEntityIdsFromLine(entityIdLines[i]);
			String[] answers = this.getAnswersFromLine(trainLines[i]);
			String entityId = "";
			String originalEntityName = entityIdLines[i].split("\t")[0];
			Map<String, Integer> relMap = new HashMap<String, Integer>();
			int maxRelTotal = 1;
			for (j = 0; j < entityIds.length; j ++) {
				String entityName = DBQuery.getEntityName(PREFIX + entityIds[j]);
				String relations = this.getAllRelationsByEntityIdAndValues(PREFIX + entityIds[j], answers);
				String[] relationArray = relations.split(",");
				int relTotal = relationArray.length;
				if (relTotal > maxRelTotal || this.isSimilarStrings(entityName, originalEntityName)) {
					maxRelTotal = relTotal;
					entityId = entityIds[j];
				}
				
				int count = 0;
				for (k = 0; k < relTotal; k++) {
					if (relationArray[k].length() < 1) {
						continue;
					}
					if (relMap.containsKey(relationArray[k])) {
						count = relMap.get(relationArray[k]);
						relMap.replace(relationArray[k], count + 1);
					} else {
						relMap.put(relationArray[k], 1);
					}
				}
			}
			
			Iterator<Entry<String, Integer>> it = relMap.entrySet().iterator();
			int maxCount = 0;
			String relation = "";
			while (it.hasNext()) {
				Entry<String, Integer> pair = it.next();
				//System.out.println(pair.getKey() + " appears " + pair.getValue() + " times");
				if (pair.getValue() > maxCount) {
					maxCount = pair.getValue();
					relation = pair.getKey();
				}
			}
			
			if (maxCount == 1) {
				relation = StringUtils.join(relMap.keySet().toArray(), ",");
			}
			
			if (entityId.length() < 1 || relation.length() < 1) {
				continue;
			}
			
			//System.out.println(entityId + " has most relations");
			//System.out.println("Relation appears most: " + relation);
			
			System.out.print(i + 1);
			System.out.print("\t" + trainLines[i].split("\t")[0]);
			System.out.print("\t" + DBQuery.getEntityName(PREFIX + entityId));
			System.out.print("\t" + entityId);
			System.out.print("\t" + relation);
			System.out.println("\t" + trainLines[i].split("\t")[2]);
		}
	}
	
	protected String[] getEntityIdsFromLine(String line) {
		Set<String> entityIdSet = new HashSet<String>();
		
		String[] subStrings = line.split("\t");
		String[] googleIds = subStrings[1].split(",");
		//exact match id
		String emId = subStrings[2];
		int i = 0;
		
		for (i = 0; i < googleIds.length; i++) {
			entityIdSet.add(googleIds[i]);
		}
		
		if (emId.charAt(0) == 'm') {
			entityIdSet.add(emId);
		}
		
		return entityIdSet.toArray(new String[0]);
	}
	
	protected String[] getAnswersFromLine(String line) {
		Set<String> answerSet = new HashSet<String>();
		
		String[] subStrings = line.split("\t");
		String[] answers = subStrings[2].split("\\|");
		int i = 0;
		
		for (i = 0; i < answers.length; i++) {
			answerSet.add(answers[i]);
		}
		
		return answerSet.toArray(new String[0]);
	}
	
	protected String getAllRelationsByEntityIdAndValues(String entityId, String[] values) {
		int i = 0;
		String relations = "";
		
		for (i = 0; i < values.length; i++) {
			String answer = values[i]; 
			if (answer.indexOf("\'") >= 0) {
				answer = answer.replace("\'", "\\'");
			}
			if (relations.length() > 0) {
				relations += ",";
			}
			relations += DBQuery.findUnaryRelationbyProperty(entityId, answer);
			int outputLength = relations.length();
			//System.out.println("----------unary done------------");
			String binaryOutput = DBQuery.findBinaryRelationByValue(entityId, answer);
			if (outputLength > 0 && binaryOutput.length() > 0) {
				relations += ",";	
			}
			relations += binaryOutput;
			//System.out.println("---------binary done-------------");
			String cvtOutput = DBQuery.findCVTRelationByValue(entityId, answer);
			if (relations.length() > 0 && cvtOutput.length() > 0) {
				relations += ",";
			}
			relations += cvtOutput;
		}
		
		return relations;
	}
	
	/**
	 * Check if search string matches target string
	 * Goal: half of search string should match
	 * 
	 * @param String s1 search string
	 * @param String s2 target string
	 * @return boolean -1 false and others true
	 */
	protected boolean isSimilarStrings(String s1, String s2) {
		boolean result = false;
		int threshold = s1.length() / 2;
		LevenshteinDistance ld = new LevenshteinDistance(threshold);
		int diff = ld.apply(s1, s2);
		
		//System.out.println(s1 + " and " + s2 + " diff is " + diff);
		
		if (diff >= 0) {
			result = true;
		}
		
		return result;
	}
	
	//-------------------Decision tree area--------------------------
	public void testDecisionTree() {
		//code example
		//http://www.emaraic.com/blog/weka-java-example
		//doc
		//http://weka.sourceforge.net/doc.stable-3-8/
	}
	//---------------------------------------------------------------
	
	public void getAnswerEntityIds() {
		String filePath = DATA_PATH + WEBQ_RELATION_OUT;
		FileReader fr = new FileReader();
		
		String[] lines = fr.getAllLinesFromFile(filePath);
		int i = 0;
		int j = 0;
		
		for (i = 0; i < lines.length; i++) {
			String qeid = PREFIX + lines[i].split("\t")[3];
			String answers = lines[i].split("\t")[5];
			String[] answerList = answers.split("\\|");
			//System.out.println("Starting processing... " + qeid + "\t" + answers);
			ArrayList<String> ansEidList = new ArrayList<String>();
			String ansEids = "";
			for (j = 0; j < answerList.length; j++) {
				String tmpEid = "";
				String answer = answerList[j].replace("\'", "\\'");
				//String unaryEids = DBQuery.findEidByUnaryRelationProperty(qeid, answer);
				//System.out.println("Unary eids: " + unaryEids);
				String binaryEids = DBQuery.findEidByBinaryRelationValue(qeid, answer);
				//System.out.println("Binary eids: " + binaryEids);
				String cvtEids = DBQuery.findEidByCVTRelationValue(qeid, answer);
				//System.out.println("CVT eids: " + cvtEids);
				if (binaryEids.length() < 1 && cvtEids.length() < 1) {
					tmpEid = DBQuery.findEntity(answer);
					//System.out.println("Exact match got eid: " + tmpEid);
				} else {
					if (binaryEids.length() > 0) {
						tmpEid = binaryEids;
						//System.out.println("Binary id got entity name: " + DBQuery.getEntityName(binaryEids));
					} else {
						tmpEid = cvtEids;
						//System.out.println("CVT id got entity name: " + DBQuery.getEntityName(cvtEids));
					}
				}
				if (tmpEid.length() > 0) {
					ansEidList.add(tmpEid.replace(PREFIX, ""));	
				}
			}
			
			if (ansEidList.size() > 0) {
				ansEids = StringUtils.join(ansEidList.toArray(new String[0]), "|");
			} else {
				ansEids = "N/A";
			}
			
			System.out.println(lines[i] + "\t" + ansEids);
		}
	}
	
	public void testWekaClassifier() {
		WekaClassifier wc = new WekaClassifier(WEKA_MODEL_PATH);
		wc.selfTest();
	}
	
	public void generateTrainingFile() {
		
		String filePath = INPUT_PATH + "webq_answerid_out_22July.txt";
		FileReader fr = new FileReader();
		APIReader ar = new APIReader();
		String[] lines = fr.getAllLinesFromFile(filePath);
		int lineNumber = lines.length;
		int i = 0;
		int j = 0;
		int k = 0;
		
		for (i = 0; i < lineNumber; i++) {
			int lineIndex = Integer.parseInt(lines[i].split("\t")[0]);
			String qeid = lines[i].split("\t")[3];
			String templateLine = this.getTemplateLineByIndex(lineIndex);
			if (templateLine.length() < 1) {
				continue;
			}
			String template = templateLine.split("\t")[1];
			String answers =  templateLine.split("\t")[4];
			String[] answerList = answers.split("\\|");

			for (j = 0; j < answerList.length; j++) {
				//System.out.println(i + "\t" + lineIndex + "\t" + template + "\t" + qeid + "\t" + answerList[j]);
				String answerEid = this.getAnswerEid(qeid, answerList[j]);
				String[] relations = this.getRelationsByIdAndValue(qeid, answerList[j]);
				for (k = 0; k < relations.length; k++) {
					//System.out.println(i + "\t" + lineIndex + "\t" + template + "\t" + qeid + "\t" + answerList[j] + "\t" + answerEid + "\t" + relations[k]);
					GKGEntity qEntity = ar.getEntity(qeid);
					if (qEntity == null) {
						continue;
					}
					Set<TypeRecord> qeTypes = qEntity.getTypes();
					Iterator<TypeRecord> it = qeTypes.iterator();
					while (it.hasNext()) {
						TypeRecord tmpTr = it.next();
						//System.out.println("Type: " + tmpTr.getType() + " has hashcode " + tmpTr.getTypeCodeStr());
						GKGEntity aEntity = ar.getEntity(answerEid);
						if (aEntity == null) {
							continue;
						}
						Set<TypeRecord> aeTypes = aEntity.getTypes();
						Iterator<TypeRecord> it2 = aeTypes.iterator();
						while (it2.hasNext()) {
							TypeRecord tmpTr2 = it2.next();
							System.out.println(
									template + ","
											+ tmpTr.getTypeCodeStr() + ","
											+ tmpTr2.getTypeCodeStr() + ","
											+ relations[k] + ","
											+ qEntity.getEntityName() + ","
											+ tmpTr.getType() + ","
											+ aEntity.getEntityName() + ","
											+ tmpTr2.getType()
											);
						}
					}
				}
			}
		}
	}
	
	protected String getTemplateLineByIndex(int index) {
		String filePath = INPUT_PATH + "webq_answerid_out__template_22July.txt";
		FileReader fr = new FileReader();
		String[] lines = fr.getAllLinesFromFile(filePath);
		int lineNumber = lines.length;
		int i = 0;
		String templateLine = "";
		
		for (i = 0; i < lineNumber; i++) {
			int lineIndex = Integer.parseInt(lines[i].split("\t")[0]);
			if (lineIndex == index) {
				templateLine = lines[i];
				break;
			}
		}
		
		return templateLine;
	}
	
	protected String[] getRelationsByIdAndValue(String entityId, String value) {
		//System.out.println("Starting process: " + entityId + "\t" + DBQuery.getEntityName(PREFIX + entityId) + "\t" + value);
		Set<String> relationList = new HashSet<String>();
		String relations = "";
		int i = 0;
		
		entityId = PREFIX + entityId;
		value = value.replace("\'", "\\'");
		relations = DBQuery.findUnaryRelationbyProperty(entityId, value);
		//System.out.println("unary relation found: " + relations);
		String[] relationArray = relations.split(",");
		for (i = 0; i < relationArray.length; i++) {
			if (relationArray[i].length() > 0) {
				relationList.add(relationArray[i]);
				//System.out.println(relationArray[i] + " added");
			}
		}
		relations = DBQuery.findBinaryRelationByValue(entityId, value);
		//System.out.println("binary relation found: " + relations);
		relationArray = relations.split(",");
		for (i = 0; i < relationArray.length; i++) {
			if (relationArray[i].length() > 0) {
				relationList.add(relationArray[i]);
				//System.out.println(relationArray[i] + " added");
			}
		}
		relations = DBQuery.findCVTRelationByValue(entityId, value);
		//System.out.println("cvt relation found: " + relations);
		relationArray = relations.split(",");
		for (i = 0; i < relationArray.length; i++) {
			if (relationArray[i].length() > 0) {
				relationList.add(relationArray[i]);
				//System.out.println(relationArray[i] + " added");
			}
		}
		
		if (relationList.size() < 1) {
			//System.out.println("None relation found");
		}
		
		return relationList.toArray(new String[0]);
	}
	
	protected String getAnswerEid(String qeid, String ansValue) {
		String aEid = "";
		String answer = ansValue.replace("\'", "\\'");
		String binaryEids = DBQuery.findEidByBinaryRelationValue(qeid, answer);
		String cvtEids = DBQuery.findEidByCVTRelationValue(qeid, answer);
		if (binaryEids.length() < 1 && cvtEids.length() < 1) {
			aEid = DBQuery.findEntity(answer);
			//System.out.println("Exact match got eid: " + tmpEid);
		} else {
			if (binaryEids.length() > 0) {
				aEid = binaryEids;
				//System.out.println("Binary id got entity name: " + DBQuery.getEntityName(binaryEids));
			} else {
				aEid = cvtEids;
				//System.out.println("CVT id got entity name: " + DBQuery.getEntityName(cvtEids));
			}
		}
		
		if (aEid.length() < 1 || aEid.indexOf(",") >= 0) {
			//System.out.println("----------------qeid format wrong------------------------");
		}
		return aEid.replace(PREFIX, "");
	}
}
