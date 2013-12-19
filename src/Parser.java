import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Parser {

	Map<String, Integer> configuration;
	ArrayList<String> memory;

	/**
	 * This parser will parse the input files & create the output files.
	 * 
	 * @param txtFiles
	 */
	public Parser(String memory_file, String configuration_file) {
		this.configuration = new HashMap<>();
		this.memory = new ArrayList<>();
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
	
	@SuppressWarnings("resource")
	public void parseConfigurationFile(String configuration_file)
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
	public void parseMemoryFile(String memory_file) throws IOException {

		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new FileReader(memory_file));
		String str = "";
		String binAddr;

		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();
			binAddr = new BigInteger(str, 16).toString(2);
			while (binAddr.length() < 32) {
				binAddr = "0" + binAddr;
			}
			this.memory.add(binAddr);
		}
	}

}
