package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileReader {
	protected String filePath = "";
	
	public FileReader() {
		this.filePath = INPUT_PATH + INPUT_FILE_NAME;
	}
	
	public String[] read() {
		ArrayList<String> nouns = new ArrayList<String>();
		
		File file = new File(this.filePath);
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				String[] subStrings = line.split("\t");
				//System.out.println(subStrings[1]);
				nouns.add(subStrings[1]);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nouns.toArray(new String[0]);
	}
	
	public String[] getWholeLine() {
		ArrayList<String> nouns = new ArrayList<String>();
		
		File file = new File(this.filePath);
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				nouns.add(line);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return nouns.toArray(new String[0]);
	}
	
	/**
	 * 
	 * @return array list of "name, id1, id2, id3, id4?"
	 */
	public String[] readEntityIdFile() {
		String filePath = INPUT_PATH + OUTPUT_ID_FILE_NAME;
		File file = new File(filePath);
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				list.add(line);
				//System.out.println(line + " got from " + filePath);
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list.toArray(new String[0]);
	}
	
	/**
	 * 
	 * @return array list of answers "ans1, ans2, etc"
	 */
	public String[] readQuestionTrainFile() {
		String filePath = DATA_PATH + QUESTION_TRAIN_FILE;
		File file = new File(filePath);
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			String line = "";
			
			while ((line = br.readLine()) != null) {
				String[] subStrings = line.split("\t");
				list.add(subStrings[0] + "\t" + subStrings[2]);
				//System.out.println(subStrings[2] + " got from " + filePath);
			}
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list.toArray(new String[0]);
	}
	
	public void searchFirstIdAppearance() {
		APIReader ar = new APIReader();
		
		String filePath = OUTPUT_PATH + OUTPUT_RELATION_FILENAME;
		
		String[] entitiesIds = this.readEntityIdFile();
		Map<Integer, String> idMap1 = new HashMap<Integer, String>();
		Map<Integer, String> idMap2 = new HashMap<Integer, String>();
		Map<Integer, String> idMap3 = new HashMap<Integer, String>();
		Map<Integer, String> idMap4 = new HashMap<Integer, String>();
		
		Map<Integer, String> resultMap = new HashMap<Integer, String>();
		
		int i = 0;
		int total = entitiesIds.length;
		for (i = 0; i < total; i++) {
			String name = entitiesIds[i].split("\t")[0];
			ar.getCategoryFromMSApi(name);
			String[] idsArray = entitiesIds[i].split("\t")[1].split(",");
			String firstId = idsArray[0];
			idMap1.put(i + 1, firstId);
			if (idsArray.length > 1) {
				String secondId = idsArray[1];
				idMap2.put(i + 1, secondId);	
			}
			if (idsArray.length > 2) {
				String thirdId = idsArray[2];
				idMap3.put(i + 1, thirdId);	
			}
			
			String fourthId = entitiesIds[i].split("\t")[2];
			if (!fourthId.equals("N/A")) {
				idMap4.put(i + 1, fourthId);	
			}
				
			
			
		}
		
		File file = new File(filePath);
		
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			String line = "";
			int countFirst = 0;
			int countSecond = 0;
			int countThird = 0;
			int countFourth = 0;
			int totalLines = 0;
			
			while ((line = br.readLine()) != null) {
				int key = Integer.parseInt(line.split("\t")[0]);
				String value = line.split("\t")[3].replace(PREFIX, "");
				if (resultMap.containsKey(key)) {
					resultMap.replace(key, resultMap.get(key) + "," + value);
				} else {
					resultMap.put(key, value);
				}
				
				totalLines++;
				int index = Integer.parseInt(line.split("\t")[0]);
				if (line.indexOf(idMap1.get(index)) > 0) {
					countFirst++;
					System.out.println("Index " + index + " contains first id " + idMap1.get(index));
				} else if (idMap2.containsKey(index) && line.indexOf(idMap2.get(index)) > 0) {
					countSecond++;
					System.out.println("Index " + index + " contains second id " + idMap2.get(index));
				} else if (idMap3.containsKey(index) && line.indexOf(idMap3.get(index)) > 0) {
					countThird++;
					System.out.println("Index " + index + " contains third id " + idMap3.get(index));
				} else if (idMap4.containsKey(index) && line.indexOf(idMap4.get(index)) > 0) {
					countFourth++;
					System.out.println("Index " + index + " contains forth id " + idMap4.get(index));
				} 
			}
			
			System.out.println("There are total " + totalLines + "."); 
			System.out.println(countFirst + " lines contains first id and " + countSecond + " lines contains second id.");
			System.out.println(countThird + " lines contains third id and " + countFourth + " lines contains fourth id.");
			System.out.println("Counters total is " + (countFirst + countSecond + countThird + countFourth));
			
			
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeDuplicatesInDBQueryOutput() {
		String filePath = OUTPUT_PATH + OUTPUT_RELATION_FILENAME;
		File file = new File(filePath);
		String line = "";
		try {
			BufferedReader br = new BufferedReader(new java.io.FileReader(file));
			
			while ((line = br.readLine()) != null) {
				String[] subStrings = line.split("\t");
				System.out.println(line);
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
