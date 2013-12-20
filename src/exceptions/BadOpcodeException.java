package exceptions;

public class BadOpcodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new BadConnectionException with a default message.
	 */
	public BadOpcodeException() {
		super("Tomosulu was unable to execute an instruction - unknown opcode!");
			
	}
	
}
