package reservationStations;

public class MulOrAddReservationStation extends ReservationStation{
	
	private float value1;
	private float value2;
	
	public MulOrAddReservationStation(int i, String name){
		super(i, name);	
	}
		
	public float getValue1() {
		return value1;
	}

	public void setValue1(float value1) {
		this.value1 = value1;
	}

	public float getValue2() {
		return value2;
	}

	public void setValue2(float value2) {
		this.value2 = value2;
	}

}
