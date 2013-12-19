import java.util.LinkedList;
import java.util.Queue;

public class Tomasulo {
	Queue<Instruction> instructions_queue;

	int pc;

	public Tomasulo() {
		instructions_queue = new LinkedList<Instruction>();
		pc = 0;
	}
	
	public void printInstructions() {
		int i = 0;
		for (Instruction instruction: this.instructions_queue) {
			System.out.println("Instruction number " + i++ + ":");
			System.out.println("OPCODE: " + instruction.OPCODE.toString());
			System.out.println("DST: " + instruction.DST);
			System.out.println("SRC0: " + instruction.SRC0);
			System.out.println("SRC1: " + instruction.SRC1);
			System.out.println("IMM: " + instruction.IMM);
		}
	}
}
