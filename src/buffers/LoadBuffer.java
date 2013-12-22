package buffers;

import main.Instruction;

public class LoadBuffer extends LoadStoreBuffer{

	private Instruction inst;
	
	public LoadBuffer(int i, String name) {	
		super(i, name);
	}
	
	public Instruction getInst() {
		return inst;
	}

	public void setInst(Instruction inst) {
		this.inst = inst;
	}

}
