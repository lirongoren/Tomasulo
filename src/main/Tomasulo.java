package main;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import buffers.LoadBuffer;
import buffers.StoreBuffer;
import cdb.CDB;
import registers.FloatRegister;
import registers.IntRegister;
import registers.Registers;
import units.ALU;
import units.FPAdd;
import units.FPMult;
import units.FPSub;
import units.Load;
import units.Store;

public class Tomasulo {
	Queue<Instruction> instructions_queue;
	Memory memory;
	Registers registers;
	
	boolean status;
	int clock;
	int pc;
	
	CDB cdb;
	
	ALU alu_unit;
	FPAdd FP_add_unit;
	FPMult FP_mult_unit;
	FPSub FP_sub_unit;
	Load load_unit;
	Store store_unit;
	
	LoadBuffer load_buffer;
	StoreBuffer store_buffer;
	
	// TODO - add fields of reservation stations

	public Tomasulo(Memory mem, Map<String, Integer> configuration) {
		instructions_queue = new LinkedList<Instruction>();
		memory = mem;
		pc = 0;
		clock = 0;
		status = Global.UNFINISHED;
		
		this.registers = new Registers();
		
		this.cdb = new CDB();
		initializeReservationStations(configuration);
		initializeBuffers(configuration);
//		initializeUnits(configuration);
	}
	
	// TODO
	private void initializeBuffers(Map<String, Integer> configuration) {
		// TODO Auto-generated method stub
		
	}

	// TODO
	private void initializeReservationStations(
			Map<String, Integer> configuration) {
		// TODO Auto-generated method stub
		
	}

	private void initializeUnits(Map<String, Integer> configuration) {
		this.alu_unit = new ALU(configuration.get("int_delay"), cdb);
		this.FP_add_unit = new FPAdd(configuration.get("add_delay"), cdb);
		this.FP_sub_unit = new FPSub(configuration.get("add_delay"), cdb);
		this.FP_mult_unit = new FPMult(configuration.get("mul_delay"), cdb);
		this.load_unit = new Load(configuration.get("mem_delay"), cdb);
		this.store_unit = new Store(configuration.get("mem_delay"), cdb);
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
	
	public void printRegistersValues() {
		for (int i = 0; i < 16; i++) {
			System.out.println("Integer Register " + i + ": " + registers.getIntRegisterValue(i));
		}
		
		for (int i = 0; i < 16; i++) {
			System.out.println("Float Register " + i + ": " + registers.getFloatRegisterValue(i));

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
