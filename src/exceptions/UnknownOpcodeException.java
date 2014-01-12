package exceptions;

public class UnknownOpcodeException extends Exception {

	private static final long serialVersionUID = 1L;
	private int pc;
	
	/**
	 * Constructs a new UnknownOpcodeException with an appropriate message.
	 */
	public UnknownOpcodeException(int pc) {
		this.pc = pc;
	}
	
	@Override
	public String getMessage() {
	    return "Tomosulu was unable to execute instruction located in memory["+pc+"] because of unknown opcode!";
	}
	
}
