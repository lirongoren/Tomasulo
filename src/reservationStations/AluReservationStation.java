package reservationStations;

public class AluReservationStation extends ReservastionStation {

	private int value1;
	private int value2;
	
	public AluReservationStation(int i, String name){
		super(i, name);	
	}
		
	public float getValue1() {
		return value1;
	}

	public void setValue1(int value1) {
		this.value1 = value1;
		this.firstTag = "";
	}

	public float getValue2() {
		return value2;
	}

	public void setValue2(int value2) {
		this.value2 = value2;
		this.secondTag = "";
	}
}
