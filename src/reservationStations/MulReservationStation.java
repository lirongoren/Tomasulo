package reservationStations;

public class MulReservationStation extends MulOrAddReservationStation{
	private String nameOfStation = "";
	
	public MulReservationStation(int i){
		super();
		nameOfStation = "MUL" + i;
	}

	public String getNameOfStation() {
		return nameOfStation;
	}
}
