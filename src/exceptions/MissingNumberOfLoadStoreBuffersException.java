package exceptions;

public class MissingNumberOfLoadStoreBuffersException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MissingLoadStoreBuffers with an appropriate message.
	 */
	public MissingNumberOfLoadStoreBuffersException() {
			
	}
	
	@Override
	public String getMessage() {
	    return "Missing number of load / store buffers in the configuration input file!";
	}
	
}
