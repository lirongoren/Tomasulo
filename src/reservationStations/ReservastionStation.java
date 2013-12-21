package reservationStations;

import main.Global;
import main.Instruction.Opcode;

public class ReservastionStation implements Comparable<ReservastionStation>{
	private Opcode opcode;
	private int busy;
	private String firstTag;
	private String secondTag;
	private String nameOfStation = "";
	
	public ReservastionStation(int i, String name) {
		busy = Global.IDLE;
		nameOfStation = name + i;
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

	public int isBusy() {
		return busy;
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
	public int compareTo(ReservastionStation o) {
		String firstRSName = this.getNameOfStation();
		String secondRSName = o.getNameOfStation();
		Integer firstAvailable = this.isBusy();
		Integer secondAvailable = o.isBusy();
		
		int result = firstAvailable.compareTo(secondAvailable);
		if (result == 0){
			result = firstRSName.compareTo(secondRSName);
		}
		return result;
	}
	
}
