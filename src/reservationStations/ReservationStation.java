package reservationStations;

import main.Global;
import main.Instruction.Opcode;

public class ReservationStation implements Comparable<ReservationStation>{
	private Opcode opcode;
	private int busy;
	protected String firstTag = "";
	protected String secondTag = "";
	private String nameOfStation = "";
	
	public ReservationStation(int i, String name) {
		busy = Global.IDLE;
		nameOfStation = name + i;
	}
	
	public boolean isReady() {
		if (firstTag.isEmpty() && secondTag.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public String getNameOfStation() {
		return nameOfStation;
	}
	
	public Opcode getOpcode() {
		return opcode;
	}

	public void setOpcode(Opcode opcode) {
		this.opcode = opcode;
	}

	public boolean isBusy() {
		return busy == Global.BUSY;
	}

	public void setBusy() {
		this.busy = Global.BUSY;
	}

	public String getFirstTag() {
		return firstTag;
	}

	public void setFirstTag(String firstTag) {
		this.firstTag = firstTag;
	}

	public String getSecondTag() {
		return secondTag;
	}

	public void setSecondTag(String secondTag) {
		this.secondTag = secondTag;
	}

	@Override
	public int compareTo(ReservationStation o) {
		String firstRSName = this.getNameOfStation();
		String secondRSName = o.getNameOfStation();
		Integer firstAvailable = this.isBusy() ? Global.BUSY : Global.IDLE;
		Integer secondAvailable = o.isBusy() ? Global.BUSY : Global.IDLE;
		
		int result = firstAvailable.compareTo(secondAvailable);
		if (result == 0){
			result = firstRSName.compareTo(secondRSName);
		}
		return result;
	}
	
}
