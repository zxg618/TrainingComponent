package trainingcomponent.utility;

import static trainingcomponent.constant.Constant.*;

import java.util.HashMap;
import java.util.List;

import trainingcomponent.io.FileReader;

public class TypeRepository {
	private static HashMap<String, String> repository = new HashMap<>();
	
	private static TypeRepository INSTANCE = null;

	// Singleton class
	private TypeRepository() {
		// Exists only to defeat instantiation.

		TypeRepository.initialise();
	}
	
	public static TypeRepository getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TypeRepository();
		}
		return INSTANCE;
	}

	private static void initialise() {
		
		String filePath = DATA_PATH + "schema-types-tabdelim_encoded.txt";
		List<String> inputLines = FileReader.read(filePath);

		for(String line : inputLines){
			String[] fields = line.split("\t");

			String typeCode = fields[0];
			String typeName = fields[1];
			
			repository.put(typeName, typeCode);
		}
	}
	
	public String getTypeCode(String type){
		return repository.get(type);
	}
	
	public static void main(String[] args) {
		System.out.println(TypeRepository.getInstance().getTypeCode("Thing"));
		
		System.out.println(TypeRepository.getInstance().getTypeCode("Place"));
		
		System.out.println(TypeRepository.getInstance().getTypeCode("Movie"));

	}

}
