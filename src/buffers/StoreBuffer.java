package buffers;

import main.Instruction;

public class StoreBuffer extends LoadStoreBuffer{

	private Instruction inst;
	
	public StoreBuffer(int i, String name) {	
		super(i, name);
	}
	
	public Instruction getInst() {
		return inst;
	}

	public void setInst(Instruction inst) {
		this.inst = inst;
	}


}
