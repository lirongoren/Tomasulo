package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import registers.Registers;
/**
 * The parser will parse the input files & create the output files.
 */
public class Parser {

	private Map<String, Integer> configuration;
	private Memory memory;

	/**
	 * Creates the configuration and the memory of the processor.
	 * @param configuration_file
	 * @param memory_file
	 */
	public Parser(String configuration_file, String memory_file) {
		this.configuration = new HashMap<String, Integer>();
		this.memory = new Memory();
		try {
			this.parseMemoryFile(memory_file);
			this.parseConfigurationFile(configuration_file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
/***************************************INPUT FILES************************************/
	/**
	 * Parses the configuration file and creates a map of configuration.
	 * @param configuration_file
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void parseConfigurationFile(String configuration_file)
			throws IOException {

		BufferedReader br = new BufferedReader(new FileReader(
				configuration_file));
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
	 * Parses the memory input file and creates the Memory.
	 * @param memory_file
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void parseMemoryFile(String memory_file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(memory_file));
		String str = "";

		while ((str = br.readLine()) != null) {
			str = str.replaceAll(" ", "").trim();
			while (str.length() < 8) {
				str = "0" + str;
			}
			memory.insert((int) Long.parseLong(str, 16));
		}
	}
	
/***************************************OUTPUT FILES************************************/
	/**
	 * Creates the memory output file.
	 * @param fileName 
	 * @throws IOException
	 */
	public void createMemoryOutputFile(String fileName) throws IOException {
		File file = new File(fileName); 
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < 1024; i++) {
			String str = memory.loadAsHexString(i);
			output.write(str);
			output.newLine();
		}
		output.close();
	}

	/**
	 * Creates the trace output file.
	 * @param instructions_queue
	 * @param fileName 
	 * @throws IOException
	 */
	public void createTraceOutputFile(Queue<Instruction> instructions_queue, String fileName) throws IOException {
		File file = new File(fileName); 
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		String hexInst;
		
		for(Instruction inst : instructions_queue){
			if (inst.getIssueCycle() < 0) {
				continue;
			}
			String line = "";
			int decInst = (int) Long.parseLong(inst.getBinaryInst(), 2);
			hexInst = Integer.toHexString(decInst);

			while (hexInst.length() < 8) {
				hexInst = "0" + hexInst;
			}
			
			line = hexInst + " " + inst.getIssueCycle() + " " + inst.getExecuteStartCycle() + " "
					+ inst.getWriteToCDBCycle();
			
			output.write(line);
			output.newLine();
		}
		output.close();
	}

	/**
	 * Creates the integer registers output file.
	 * @param registers
	 * @param fileName 
	 * @throws IOException
	 */
	public void createIntRegistersOutputFile(Registers registers, String fileName) throws IOException {
		File file = new File(fileName); 
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		registers.printIntRegisters(output);
		output.close();
	}
	
	/**
	 * Creates the float registers output file.
	 * @param registers
	 * @param fileName 
	 * @throws IOException
	 */
	public void createFloatRegistersOutputFile(Registers registers, String fileName) throws IOException {
		File file = new File(fileName); 
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		registers.printFloatRegisters(output);
		output.close();
	}

/***************************************Getters & Setters *************************************/
	
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
