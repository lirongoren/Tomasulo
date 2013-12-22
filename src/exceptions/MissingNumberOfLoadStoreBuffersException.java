package exceptions;

public class MissingNumberOfLoadStoreBuffersException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MissingLoadStoreBuffers with a default message.
	 */
	public MissingNumberOfLoadStoreBuffersException() {
		super("Missing number of load / store buffers in the configuration input file!");
			
	}
}
