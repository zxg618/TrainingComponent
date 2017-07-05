package trainingcomponent.service;

import java.util.ArrayList;

import trainingcomponent.database.DBQuery;
import trainingcomponent.io.APIReader;
import trainingcomponent.io.FileReader;

public class TrainingComponentService {
	public void getAllIds() {
		//this.getAllNamesFromFile();
		this.verifyCurrentIds();
	}
	
	public void testQuery() {
		DBQuery.dbQueryTest();
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
