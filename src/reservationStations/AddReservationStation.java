package reservationStations;

public class AddReservationStation extends MulOrAddReservationStation {
	private String nameOfStation = "";
	
	public AddReservationStation(int i){
		super();
		nameOfStation = "ADD" + i;
	}

	public String getNameOfStation() {
		return nameOfStation;
	}
}
