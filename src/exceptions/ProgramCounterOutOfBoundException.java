package exceptions;

public class ProgramCounterOutOfBoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new AddressOutOfMemory with a default message.
	 */
	public ProgramCounterOutOfBoundException() {
		super("Program counter is out of memory bounds!");
			
	}
}
