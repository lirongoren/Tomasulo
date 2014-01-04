package buffers;

import reservationStations.ReservationStation;

public class LoadStoreBuffer extends ReservationStation{
	
	private int address;
	private int value1; // for load and store instructions
	private float value2; // for store instructions only
	
	public LoadStoreBuffer(int i, String name) {
		super(i, name);
	}
	
	public void calculateAddress(int imm, int registerValue){
		address = imm + registerValue;
	}
	
	public int getAddress() {
		return address;
	}
	
	public void setValue1(int value1) {
		this.value1 = value1;
		this.secondTag = "";
	}

	public void setValue2(float value2) {
		this.value2 = value2;
		this.secondTag = "";
	}
	
	public int getValue1() {
		return value1;
	}

	public float getValue2() {
		return value2;
	}


}
