package exceptions;

public class MissingLoadStoreBuffers extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MissingLoadStoreBuffers with a default message.
	 */
	public MissingLoadStoreBuffers() {
		super("Missing number of load / store buffers in the configuration input file!");
			
	}
}
