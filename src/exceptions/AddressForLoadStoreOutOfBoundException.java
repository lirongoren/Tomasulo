package exceptions;

import main.Instruction.Opcode;

public class AddressForLoadStoreOutOfBoundException extends Exception{

	private static final long serialVersionUID = 1L;
	private int pc;
	private int address;
	private Opcode opcode;
	
	/**
	 * Constructs a new AddressOutOfMemory with an appropriate message.
	 * @param clock 
	 * @param pc 
	 */
	public AddressForLoadStoreOutOfBoundException(int pc, int address ,Opcode opcode) {
		this.pc = pc;
		this.address = address;
		this.opcode = opcode;
	}
	
	@Override
	public String getMessage() {
	    return "The " + opcode + " instruction in Memory["+pc+"] tried to access Memory["+address+"] which exceed the memory bounds!";
	}
}
