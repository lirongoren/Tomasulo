
public class Instruction {
	//tal
	String name = "";

    int issue = 0;
    int exec = 0;
    int writeback = 0;

    int op = 0;
    int des = 0;
    int src1 = 0;
    int src2 = 0;
    int signedImm = 0;
    
    int station = 0;

    float result = 0f;

    public Instruction(String s){
            name = s;
            
            //TO DO: calculating op, des, src1, src2, signedImm variables.
    } 
    
    public enum Opcode {
    	
		LD("LD"),
		ST("ST"),
		JUMP("JUMP"),
		BEQ("BEQ"),
		BNE("BNE"),
		ADD("ADD"),
		ADDI("ADDI"),
		SUB("SUB"),
		SUBI("SUBI"),
		ADD_S("ADD_S"),
		SUB_S("SUB_S"),
		MULT_S("MULT_S"),
		HALT("HALT");

		private String opcode;

		private Opcode(String opcode) {
			this.opcode = opcode;
		}

		@Override
		public String toString() {
			return opcode;
		}
	}

}
