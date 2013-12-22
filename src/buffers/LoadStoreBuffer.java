package buffers;

import reservationStations.ReservastionStation;

public class LoadStoreBuffer extends ReservastionStation{
	
	private int address;
		
	public LoadStoreBuffer(int i, String name) {
		super(i, name);
	}
	
	public void calculateAddress(int imm, int registerValue){
		address = imm + registerValue;
	}
	
	public void setAddress(int address) {
		this.address = address;
	}

}
