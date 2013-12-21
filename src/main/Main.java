package main;
import java.io.FileNotFoundException;

import exceptions.MisssingReservationsException;
import exceptions.UnknownOpcodeException;

public class Main {

	public static void main(String[] args) throws FileNotFoundException, UnknownOpcodeException, MisssingReservationsException {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(),
				parser.getConfiguration());

		// for (String instruction_str : parser.memory) {
		// instruction = new Instruction(instruction_str);
		// tomasulo.instructions_queue.add(instruction);
		// }
		while (!tomasulo.isFinished()) {
			tomasulo.step();
		}
		
		tomasulo.printInstructions();
		tomasulo.printRegistersValues();
	
		parser.createMemoryOutputFile();
	}
}
