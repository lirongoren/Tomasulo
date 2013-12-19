package main;
import java.io.FileNotFoundException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(),
				parser.getConfiguration());

		// for (String instruction_str : parser.memory) {
		// instruction = new Instruction(instruction_str);
		// tomasulo.instructions_queue.add(instruction);
		// }

		tomasulo.printInstructions();
		tomasulo.printRegistersValues();
		
		while (!tomasulo.isFinished()) {
			tomasulo.step();
		}

		parser.createMemoryOutputFile();

	}

}
