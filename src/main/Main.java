package main;
import java.io.IOException;
import exceptions.*;

public class Main {

	public static void main(String[] args) throws UnknownOpcodeException, IOException,
	MissingNumberOfReservationStationsException, MissingNumberOfLoadStoreBuffersException {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = new Tomasulo(parser.getMemory(), parser.getConfiguration());

//		while (!tomasulo.isFinished()) {
//			tomasulo.step();
//		}
		
		parser.createMemoryOutputFile();
		parser.createFloatRegistersOutputFile(tomasulo.getRegisters());
		parser.createIntRegistersOutputFile(tomasulo.getRegisters());
		parser.createTraceOutputFile(tomasulo.getInstructionsStaticQueue());
		
		tomasulo.printInstructions();
		tomasulo.printRegistersValues();	
	}
	
	
}
