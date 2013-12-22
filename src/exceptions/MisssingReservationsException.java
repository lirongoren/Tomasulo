package exceptions;

public class MisssingReservationsException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MisssingReservationsException with a default message.
	 */
	public MisssingReservationsException() {
		super("Missing value for reservation stations in the configuration input file!");
			
	}
}
