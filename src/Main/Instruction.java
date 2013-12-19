package Main;
public class Instruction {
	
	String name = "";

	int issue = 0;
	int exec = 0;
	int writeback = 0;

	Opcode OPCODE = Opcode.LD;
	int DST = 0;
	int SRC0 = 0;
	int SRC1 = 0;
	int IMM = 0;

	int station = 0;

	float result = 0f;

	public Instruction(String instruction) {
		name = instruction;
		OPCODE.value = getOpcodeValue();
		DST = getDestinationValue();
		SRC0 = getFirstSourceValue();
		SRC1 = getSecondSourceValue();
		IMM = getImmValue();
	}

	public int getImmValue() {
		return Integer.parseInt(name.substring(16, 32), 2);
	}
	
	public int getSecondSourceValue() {
		return Integer.parseInt(name.substring(12, 16), 2);
	}
	
	public int getFirstSourceValue() {
		return Integer.parseInt(name.substring(8, 12), 2);
	}
	
	public int getDestinationValue() {
		return Integer.parseInt(name.substring(4, 8), 2);
	}

	public int getOpcodeValue() {
		return Integer.parseInt(name.substring(0, 4), 2);
	}

	public enum Opcode {
		LD(0), ST(1), JUMP(2), BEQ(3), BNE(4), ADD(5), ADDI(6), SUB(7), SUBI(8), ADD_S(
				9), SUB_S(10), MULT_S(11), HALT(12);

		private int value;

		private Opcode(int value) {
			this.value = value;
		}

		@Override
		public String toString() {
			switch (this.value) {
			case 0:
				return "LD";
			case 1:
				return "ST";
			case 2:
				return "JUMP";
			case 3:
				return "BEQ";
			case 4:
				return "BNE";
			case 5:
				return "ADD";
			case 6:
				return "ADDI";
			case 7:
				return "SUB";
			case 8:
				return "SUBI";
			case 9:
				return "ADD_S";
			case 10:
				return "SUB_S";
			case 11:
				return "MULT_S";
			case 12:
				return "HALT";
			default:
				break;
			}
			return null;
		}
	}

}
