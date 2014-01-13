package exceptions;

public class InvalidDelayValueForUnitsException extends Exception {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new MisssingReservationsException with an appropriate message.
	 */
	public InvalidDelayValueForUnitsException() {
	}
	
	@Override
	public String getMessage() {
	    return "Missing positive value for some unit in the configuration input file!";
	}
}
