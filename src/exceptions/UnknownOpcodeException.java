package exceptions;

public class UnknownOpcodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new UnknownOpcodeException with a default message.
	 */
	public UnknownOpcodeException() {
		super("Tomosulu was unable to execute an instruction because of unknown opcode!");
			
	}
	
}
