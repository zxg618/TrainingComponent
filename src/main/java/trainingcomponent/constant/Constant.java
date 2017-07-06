package trainingcomponent.constant;

public final class Constant {
	//google related
	public static final String KGS_API = "https://kgsearch.googleapis.com/v1/entities:search";
	public static final String API_KEY = "AIzaSyCGXgm6ELaHoUc5h5PiBLvTeR8Y-q2z3y4";
	public static final String API_PARAM_QUERY = "query";
	public static final String API_PARAM_LIMIT = "limit";
	public static final String API_PARAM_IDENT = "indent";
	public static final String API_PARAM_KEY = "key";
	
	//input and output path
	public static final String INPUT_PATH = "./input/";
	public static final String OUTPUT_PATH = "./output/";
	public static final String DATA_PATH = "./data/";
	public static final String INPUT_FILE_NAME = "tab_delim_train_step1.txt";
	//public static final String INPUT_FILE_NAME = "tab_delim_test_step1.txt";
	public static final String OUTPUT_ID_FILE_NAME = "EntitiesIds.txt";
	//public static final String OUTPUT_ID_FILE_NAME = "EntitiesIds.txt";
	
	public static String PREFIX = "http://rdf.freebase.com/ns/";
	
	public static String QUESTION_TRAIN_FILE = "tab_delim_train.txt";
	
	public static int START = 1500;
}
