package trainingcomponent.service;
import static trainingcomponent.constant.Constant.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringEscapeUtils;

import trainingcomponent.database.DBQuery;
import trainingcomponent.io.APIReader;
import trainingcomponent.io.FileReader;

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
}
