package exceptions;

public class MissingNumberOfReservationStationsException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MisssingReservationsException with a default message.
	 */
	public MissingNumberOfReservationStationsException() {
		super("Missing value for reservation stations in the configuration input file!");
			
	}
}
