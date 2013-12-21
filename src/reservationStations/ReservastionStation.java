package reservationStations;

import main.Global;
import main.Instruction.Opcode;

public class ReservastionStation {
	private Opcode opcode;
	private int busy;
	private String firstTag;
	private String secondTag;
	
	public ReservastionStation() {
		busy = Global.IDLE;
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
	
}
