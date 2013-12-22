package main;
import java.io.IOException;

import exceptions.MisssingReservationsException;
import exceptions.UnknownOpcodeException;

public class Main {

	public static void main(String[] args) throws UnknownOpcodeException, MisssingReservationsException, IOException {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(), parser.getConfiguration());

		while (!tomasulo.isFinished()) {
			tomasulo.step();
		}
		
		parser.createMemoryOutputFile();
		tomasulo.printInstructions();
		tomasulo.printRegistersValues();
	}
}
