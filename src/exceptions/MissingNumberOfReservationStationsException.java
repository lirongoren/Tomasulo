package exceptions;

public class MissingNumberOfReservationStationsException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MisssingReservationsException with an appropriate message.
	 */
	public MissingNumberOfReservationStationsException() {
	}
	
	@Override
	public String getMessage() {
	    return "Missing value for reservation stations in the configuration input file!";
	}
}
