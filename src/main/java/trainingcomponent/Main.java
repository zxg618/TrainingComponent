package trainingcomponent;

import trainingcomponent.service.TrainingComponentService;

public class Main {

	public static void main(String[] args) {
		TrainingComponentService tcService = new TrainingComponentService();
		tcService.getAllIds();
		tcService.testQuery();
	}

}
