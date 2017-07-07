package trainingcomponent.service;
import static trainingcomponent.constant.Constant.*;
import java.util.ArrayList;

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
		
		for (i = 0; i < answerLength; i++) {
			for (j = 0; j < idLength; j++) {
				String line = "";
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
				relation += ",";
				//System.out.println("----------unary done------------");
				relation += DBQuery.findBinaryRelationByValue(eid, answer);
				relation += ",";
				//System.out.println("---------binary done-------------");
				relation += DBQuery.findCVTRelationByValue(eid, answer);
				//System.out.println("--------cvt done--------------");
				if (relation.length() > 5) {
					line += relation + "\t";	
				} else {
					printFlag = false;
				}
				
				if (answer.indexOf("\\'") >= 0) {
					answer = answer.replace("\\'", "\'");
				}
				line += answer;
				if (printFlag) {
					System.out.println(line);
					break;
				} else {
					//test purpose
					//System.out.println(line);
					printFlag = true;
				}
			}
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
}
