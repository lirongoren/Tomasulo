package buffers;
import main.Instruction;
import reservationStations.ReservationStation;

public class LoadStoreBuffer extends ReservationStation {
	
	private int address;
	private int value1; // for load and store instructions
	private float value2; // for store instructions only
	private Instruction inst;
	

	public LoadStoreBuffer(int i, String name) {
		super(i, name);
		address = -1;
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

	
	public Instruction getInst() {
		return inst;
	}

	public void setInst(Instruction inst) {
		this.inst = inst;
	}

	public void freeBuffer() {
		address = -1;
		free();
	}
	
}
