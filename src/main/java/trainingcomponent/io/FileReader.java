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
}
