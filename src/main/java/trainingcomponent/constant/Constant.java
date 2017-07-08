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
	public static final String OUTPUT_RELATION_FILENAME = "dbquery_output copy.txt";
	//public static final String OUTPUT_ID_FILE_NAME = "EntitiesIds.txt";
	
	public static String PREFIX = "http://rdf.freebase.com/ns/";
	
	public static String QUESTION_TRAIN_FILE = "tab_delim_train.txt";
	
	public static int START = 0;
	
	//microsoft api related
	public static String HOST = "https://concept.research.microsoft.com/";
	
	//instance={instance}&topK={topK}
	public static String PCE_API = "api/Concept/ScoreByProb?";
	
	//instance={instance}&topK={topK}&smooth={smooth}
	public static String MI_API = "api/Concept/ScoreByMI?";
	
	//instance={instance}&topK={topK}&smooth={smooth}
	public static String PEC_API = "api/Concept/ScoreByTypi?";
	
	//instance={instance}&topK={topK}&smooth={smooth}
	public static String NPMI_API = "GET api/Concept/ScoreByNPMI?";
	
	//instance={instance}&topK={topK}&pmiK={pmiK}&smooth={smooth}
	public static String PMIK_API = "api/Concept/ScoreByPMIK?";
	
	//instance={instance}&topK={topK}&pmiK={pmiK}&smooth={smooth}
	public static String BLC_API = "api/Concept/ScoreByCross?";
	
	public static String PARA_INSTANCE = "instance";
	public static String PARA_TOPK = "tokK";
	public static String PARA_SMOOTH = "smooth";
	public static String PARA_PMIK = "pmiK";
}
