package Main;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import registers.FloatRegister;
import registers.IntRegister;
import registers.Registers;

public class Tomasulo {
	Queue<Instruction> instructions_queue;
	Memory memory;
	Registers registers;
	
	boolean status;
	int clock;
	int pc;

	public Tomasulo(Memory mem, Map<String, Integer> configuration) {
		instructions_queue = new LinkedList<Instruction>();
		memory = mem;
		pc = 0;
		clock = 0;
		status = Global.UNFINISHED;
		
		this.registers = new Registers();
	}
	
	//TODO
	public void issue(){	
	}
	
	//TODO 
    public void execute(){ 
    }
    
    //TODO
    public void writeback(){ 
    } 

	
	public void printInstructions() {
		int j = 0;
		Instruction inst;
		String str;
		
		for (int i=0; i<2; i++) {
			System.out.println("Instruction number " + i + ":");
			str = memory.getInst (j++);	
			inst = new Instruction(str);
		
			System.out.println("OPCODE: " + inst.OPCODE.toString());
			System.out.println("DST: " + inst.DST);
			System.out.println("SRC0: " + inst.SRC0);
			System.out.println("SRC1: " + inst.SRC1);
			System.out.println("IMM: " + inst.IMM);
		}
	}
	
	//Getters & Setters:
	public boolean isFinished() {
		return status;
	}

	
	//TODO
	public void step() {
	}
}
