package buffers;

import reservationStations.ReservationStation;

public class LoadStoreBuffer extends ReservationStation{
	
	private int address;
	private int value1;
	private int value2;
	
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
	}

	public void setValue2(int value2) {
		this.value2 = value2;
	}
	
	public int getValue1() {
		return value1;
	}

	public int getValue2() {
		return value2;
	}


}
