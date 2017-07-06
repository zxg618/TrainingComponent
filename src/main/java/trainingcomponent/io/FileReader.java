package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;

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
}
