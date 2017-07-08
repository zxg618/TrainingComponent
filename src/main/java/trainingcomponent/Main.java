package trainingcomponent;

//import org.apache.commons.lang3.StringEscapeUtils;

import trainingcomponent.service.TrainingComponentService;

public class Main {

	public static void main(String[] args) {
		TrainingComponentService tcService = new TrainingComponentService();
		//tcService.getAllIds();
		tcService.testQuery();
		//tcService.verifyFirstIdOfEachEntity();
		//tcService.removeDuplicates();
//		String str = "Padm\u00e9 Amidala";
//		System.out.println("original string is " + str);
//		str = str.replace("\\", "\\\\");
//		str = StringEscapeUtils.unescapeJava(str);
//		System.out.println("escaped string is " + str);
	}

}
