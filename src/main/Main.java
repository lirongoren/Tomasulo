package main;
import java.io.IOException;

import exceptions.*;

public class Main {

	public static void main(String[] args) {

		Parser parser = new Parser(args[0], args[1]);
		Tomasulo tomasulo = null;
		
		try {
			tomasulo = new Tomasulo(parser.getMemory(), parser.getConfiguration());
		}
		catch (MissingNumberOfLoadStoreBuffersException | MissingNumberOfReservationStationsException  e) {
			terminateProgram(e.getMessage());
			return;
		}
			
		try {
			tomasulo.printInstructions();
		} catch (UnknownOpcodeException | ProgramCounterOutOfBoundException e) {
			terminateProgram(e.getMessage());
		}	
		
		while (!tomasulo.isFinished()) {
			try {
				tomasulo.step();
			} catch (ProgramCounterOutOfBoundException | UnknownOpcodeException e) {
				terminateProgram(e.getMessage());
				tomasulo.terminateTomasulu();
			}
			tomasulo.printRegistersValues();
		}
		
		try {
			parser.createMemoryOutputFile();
			parser.createFloatRegistersOutputFile(tomasulo.getRegisters());
			parser.createIntRegistersOutputFile(tomasulo.getRegisters());
			parser.createTraceOutputFile(tomasulo.getInstructionsStaticQueue());
		} catch (IOException e) {
			terminateProgram("Got an IO exception, could not create output files.");
		}
				
		
	}
	
	public static void terminateProgram (String message){
		System.out.println(message);
		System.out.println("Terminating Program...");
	}
	
}
