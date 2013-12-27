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
			this.parseMemoryFile(memory_file);
			this.parseConfigurationFile(configuration_file);

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
			while (str.length() < 8) {
				str = "0" + str;
			}
			memory.insert((int) Long.parseLong(str, 16));
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	public void createMemoryOutputFile() throws IOException {
		File file = new File("memout.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < 1024; i++) {
			String str = Integer.toHexString(memory.load(i));
			while (str.length() < 8) {
				str = "0" + str;
			}
			output.write(str);
			output.newLine();
		}
		output.close();
	}

	// TODO
	public void createTraceOutputFile(Queue<Instruction> instructions_queue)
			throws IOException {
		File file = new File("memout.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		for (int i = 0; i < 1024; i++) {
			String str = Integer.toHexString(memory.load(i));
			while (str.length() < 8) {
				str = "0" + str;
			}
			output.write(str);
			output.newLine();
		}
	}

	// TODO
	public void createIntRegistersOutputFile()
			throws IOException {
	}

	// TODO
	public void createFloatRegistersOutputFile(Registers registers) throws IOException {
		File file = new File("regout.txt");
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
		registers.printFloatRegisters(output);
	}

	// Getters & Setters
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
