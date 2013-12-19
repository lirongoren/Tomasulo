package Main;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Parser {

	private Map<String, Integer> configuration;
	private Memory memory;

	/**
	 * This parser will parse the input files & create the output files.
	 * 
	 * @param txtFiles
	 */
	public Parser(String memory_file, String configuration_file) {
		this.configuration = new HashMap<String, Integer>();
		this.memory = new Memory();
		try {
			this.parseConfigurationFile(configuration_file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			this.parseMemoryFile(memory_file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param configuration_file
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void parseConfigurationFile(String configuration_file)
			throws IOException {

		BufferedReader br = new BufferedReader(
				new FileReader(configuration_file));
		String str = "";

		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();
			String[] s = str.split("=");
			if (s.length == 2) {
				this.configuration.put(s[0], Integer.parseInt(s[1]));
			}
		}
	}

	/**
	 * 
	 * @param memory_file
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void parseMemoryFile(String memory_file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(memory_file));
		String str = "";
				
		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();
//			binAddr = new BigInteger(str, 16).toString(2);			
			while (str.length() < 8) {
				str = "0" + str;
			}
			
			for (int i=0,k=0; k<4; i=i+2, k++){
				String s = str.substring(i, i+2);
				memory.insert(Integer.parseInt(s, 16));
			}
		}
	}

	
	//TODO
	public void createMemoryOutputFile(){
	}
	
	//TODO
	public void createTraceOutputFile(Queue<Instruction> instructions_queue){
	}
	
	//TODO
	public void createIntRegistersOutputFile(){
	}
	
	//TODO
	public void createFloatRegistersOutputFile(){
	}
	
	//Getters & Setters
	public Map<String, Integer> getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Map<String, Integer> configuration) {
		this.configuration = configuration;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}
	
	
}
