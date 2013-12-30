package buffers;

import reservationStations.ReservationStation;

public class LoadStoreBuffer extends ReservationStation{
	
	private int address;
		
	public LoadStoreBuffer(int i, String name) {
		super(i, name);
	}
	
	public void calculateAddress(int imm, int registerValue){
		address = imm + registerValue;
	}
	
	public int getAddress() {
		return address;
	}

}
