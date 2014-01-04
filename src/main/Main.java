package main;
import java.io.IOException;

import exceptions.*;

public class Main {

	public static void main(String[] args) throws UnknownOpcodeException, IOException,
	MissingNumberOfReservationStationsException, MissingNumberOfLoadStoreBuffersException, ProgramCounterOutOfBoundException {

		Parser parser = new Parser(args[1], args[0]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(), parser.getConfiguration());

//		tomasulo.printInstructions();
		
		while (!tomasulo.isFinished()) {
			tomasulo.step();
		}
		
		parser.createMemoryOutputFile();
		parser.createFloatRegistersOutputFile(tomasulo.getRegisters());
		parser.createIntRegistersOutputFile(tomasulo.getRegisters());
		parser.createTraceOutputFile(tomasulo.getInstructionsStaticQueue());
		
//		tomasulo.printInstructions();
		tomasulo.printRegistersValues();	
	}
	
	
}
