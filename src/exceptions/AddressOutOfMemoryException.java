package exceptions;

public class AddressOutOfMemoryException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new AddressOutOfMemory with a default message.
	 */
	public AddressOutOfMemoryException() {
		super("Address for load / store operation is out of memory!");
			
	}
	
}
