package exceptions;

public class ProgramCounterOutOfBoundException extends Exception{

	private static final long serialVersionUID = 1L;
	private int pc;
	private int clock;
	
	/**
	 * Constructs a new AddressOutOfMemory with an appropriate message.
	 * @param clock 
	 * @param pc 
	 */
	public ProgramCounterOutOfBoundException(int pc, int clock) {
		this.pc = pc;
		this.clock = clock;
	}
	
	@Override
	public String getMessage() {
	    return "On clock cycle " + clock + " the program counter got to " + pc + " and exceed the memory bounds!";
	}
	
}
