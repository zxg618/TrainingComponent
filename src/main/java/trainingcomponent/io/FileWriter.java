package trainingcomponent.io;

import static trainingcomponent.constant.Constant.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileWriter {
	protected String idsFilePath = OUTPUT_PATH + OUTPUT_ID_FILE_NAME;
	protected java.io.FileWriter fw = null;
	protected BufferedWriter bw = null;
	protected PrintWriter pw = null;
	
	public FileWriter() {
		try {
			this.fw = new java.io.FileWriter(this.idsFilePath, true);
			this.bw = new BufferedWriter(this.fw);
			this.pw = new PrintWriter(this.bw);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeIdsToOutputFile(String line) {
		System.out.print("Writing [" + line + "] to file " + this.idsFilePath + "\n");
		this.pw.print(line);
	}
	
	public void close() {
		  try {
		        if(pw != null)
		            pw.close();
		    } catch (Exception e) {
		        //exception handling left as an exercise for the reader
		    }
		    try {
		        if(bw != null)
		            bw.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    }
		    try {
		        if(fw != null)
		            fw.close();
		    } catch (IOException e) {
		        //exception handling left as an exercise for the reader
		    }
	}
}
