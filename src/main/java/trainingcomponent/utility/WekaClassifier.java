package trainingcomponent.utility;

import static trainingcomponent.constant.Constant.*;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class WekaClassifier 
{
	String modelPath = "";
	Classifier classifier = null;
	
	public WekaClassifier(String modelPath) {
		this.modelPath = modelPath;
		try {
			this.classifier = (Classifier) weka.core.SerializationHelper.read(modelPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public void selfTest() {
		String testFilePath = INPUT_PATH + "modelTest.arff";
		
		try {
			DataSource source = new DataSource(testFilePath);
			Instances testSets = source.getDataSet();
			testSets.setClassIndex(testSets.numAttributes() - 1);
			 for (int i = 0; i < testSets.numInstances(); i++) {
				 System.out.println(i + ". " + testSets.instance(i).toString());
				   double clsLabel = this.classifier.classifyInstance(testSets.instance(i));
				   testSets.instance(i).setClassValue(clsLabel);
				   System.out.println(clsLabel + " -> " + testSets.classAttribute().value((int) clsLabel));
				   System.out.println(testSets.instance(i).toString());
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
