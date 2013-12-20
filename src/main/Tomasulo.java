package main;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import buffers.LoadBuffer;
import buffers.StoreBuffer;
import cdb.CDB;
import registers.Registers;
import units.*;

public class Tomasulo {
	
	private Queue<Instruction> instructions_queue;
	private Memory memory;
	private Registers registers;
	
	
	boolean status;
	int clock;
	int pc;
	
	private CDB cdb;
	
	private integerALU alu_unit;
	private FPAddSub FP_add_sub_unit;
	private FPMult FP_mult_unit;
	private LoadStore load_store_unit;

	private LoadBuffer loadBuffer;
	private StoreBuffer storeBuffer;
	
	// TODO - add fields of reservation stations

	public Tomasulo(Memory mem, Map<String, Integer> configuration) {
		instructions_queue = new LinkedList<Instruction>();
		memory = mem;
		pc = 0;
		clock = 0;
		status = Global.UNFINISHED;
		
		registers = new Registers();
	
		cdb = new CDB();
		initializeReservationStations(configuration);
		initializeBuffers(configuration);
		initializeUnits(configuration);
	}
	
	/**
	 * 
	 * @param configuration
	 */
	private void initializeBuffers(Map<String, Integer> configuration) {
		try{
			loadBuffer = new LoadBuffer(configuration.get("mem_nr_load_buffers"));
		}
		catch (NullPointerException e) {
			loadBuffer = new LoadBuffer();
		}
		try{
			storeBuffer = new StoreBuffer(configuration.get("mem_nr_store_buffers"));
		}
		catch (NullPointerException e) {
			storeBuffer = new StoreBuffer();

		}
	}

	// TODO
	private void initializeReservationStations(Map<String, Integer> configuration) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param configuration
	 */
	private void initializeUnits(Map<String, Integer> configuration) {
		try{
			alu_unit = new integerALU(configuration.get("int_delay"), cdb);
		}
		catch (NullPointerException e) {
			alu_unit = new integerALU(cdb);
		}
		
		try{
			FP_add_sub_unit = new FPAddSub(configuration.get("add_delay"), cdb);
		}
		catch (NullPointerException e) {
			FP_add_sub_unit = new FPAddSub(cdb);
		}
		
		try{
			FP_mult_unit = new FPMult(configuration.get("mul_delay"), cdb);
		}
		catch (NullPointerException e) {
			FP_mult_unit = new FPMult(cdb);
		}
		
		try{
			load_store_unit = new LoadStore(configuration.get("mem_delay"), cdb);
		}
		catch (NullPointerException e) {
			load_store_unit = new LoadStore(cdb);
		}
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

    /**
	 * This is a test method.
	 */
	public void printInstructions() {
		System.out.println("Input Instructions:\n");
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
			System.out.println();
		}
	}
	
	/**
	 * This is a test method.
	 */
	public void printRegistersValues() {
		System.out.println("Integer registers values:\n");
		for (int i = 0; i < 16; i++) {
			System.out.println("Integer Register " + i + ": " + registers.getIntRegisterValue(i));
		}
		System.out.println();
		System.out.println("Float registers values:\n");
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
