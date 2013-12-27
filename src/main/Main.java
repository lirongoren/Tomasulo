package main;
import java.io.IOException;
import java.io.PrintWriter;

import exceptions.*;

public class Main {

	public static void main(String[] args) throws UnknownOpcodeException, IOException,
	MissingNumberOfReservationStationsException, MissingNumberOfLoadStoreBuffersException {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(), parser.getConfiguration());

		while (!tomasulo.isFinished()) {
			tomasulo.step();
		}
		
		parser.createMemoryOutputFile();
		parser.createFloatRegistersOutputFile(tomasulo.getRegisters());
//		parser.createTraceOutputFile(tomasulo.);
		tomasulo.printInstructions();
		tomasulo.printRegistersValues();
	
	
	}
}
