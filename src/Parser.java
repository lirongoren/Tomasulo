import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Parser {

	/**
	 * This parser will parse the input files & create the output files.
	 * 
	 * @param txtFiles
	 */
	public Parser() {
	}

	@SuppressWarnings("resource")
	public Map<String, Integer> parseConfigurationFile(String configurationFile) throws IOException {
		
		Map<String, Integer> parametersValue = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new FileReader(configurationFile));
		String str = "";
		
		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();
			String[] s = str.split("=");
			if (s.length == 2){
				parametersValue.put(s[0], Integer.parseInt(s[1]));
			}
		}
		return parametersValue;
	}

	/**
	 * 
	 * @param memoryInputFile
	 * @throws IOException
	 */
	public void parseMemoryInputFile(String memoryInputFile) throws IOException{
		
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(memoryInputFile));
		String str = "";
		
		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();	
			String binAddr = new BigInteger(str, 16).toString(2);
		}	
	}
	
	
	
	
}
