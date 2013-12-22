package main;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import main.Instruction.Opcode;
import registers.Registers;
import reservationStations.ReservationStations;
import units.*;
import buffers.*;
import cdb.CDB;
import exceptions.*;

public class Tomasulo {
	
	private Queue<Instruction> instructions_queue;
	private List<Instruction> execList;
	private List<Instruction> wbList;
	
	private Memory memory;
	private Registers registers;
	private ReservationStations reservationStations;
	private Buffers buffers;
	
	private boolean status;
	private int clock;
	private int pc;
	
	private CDB cdb;
	
	private integerALU alu_unit;
	private FPAddSub FP_add_sub_unit;
	private FPMul FP_mult_unit;
	private LoadStore load_store_unit;
	
	/**
	 * 
	 * @param mem
	 * @param configuration
	 * @throws MisssingNumberOfReservationStationsException 
	 * @throws MissingNumberOfLoadStoreBuffers 
	 * @throws MisssingReservationsException
	 * @throws MissingLoadStoreBuffers 
	 */
	public Tomasulo(Memory mem, Map<String, Integer> configuration)
			throws MissingNumberOfReservationStationsException, MissingNumberOfLoadStoreBuffersException{	
		instructions_queue = new LinkedList<Instruction>();
		execList = new ArrayList<Instruction>();     
        wbList = new ArrayList<Instruction>(); 
		
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
	 * @throws UnknownOpcodeException
	 */
	public void step() throws UnknownOpcodeException {
		Instruction inst = fetchInstruction();
		if (!inst.OPCODE.equals(Opcode.HALT) && pc<memory.getMaxWords()-1){
			instructions_queue.add(inst);
									
			issue();
			execute();		 
	        writeback(); 
		}
		
		else if (inst.OPCODE.equals(Opcode.HALT)){
			System.out.println("Instruction number " + pc + " is an HALT Operation");
			status = Global.FINISHED;
		}
		
		else if (pc == memory.getMaxWords()-1){
			System.out.println("Missing Halt Operation.\nContinue Executing Legal Instructions: ");
			status = Global.FINISHED;
		}
	}
	
	/**
	 * Fetching an instruction from the memory to the Instruction Queue takes one clock cycle.
	 * @return
	 * @throws UnknownOpcodeException
	 */
	private Instruction fetchInstruction() throws UnknownOpcodeException {
		Instruction inst = new Instruction(memory.getInst(pc++));
		clock++;	
		return inst;
	}
	
	/**
	 * For JUMP instructions.
	 */
	private void emptyInstructionsQueue(){
		instructions_queue.clear();
	}
	
	/**
	 * 
	 * @param configuration
	 * @throws MissingLoadStoreBuffers 
	 */
	private void initializeBuffers(Map<String, Integer> configuration)
			throws MissingNumberOfLoadStoreBuffersException {
		int numLoadBuffers;
		int numStoreBuffers;
		try{
			numLoadBuffers = configuration.get("mem_nr_load_buffers");
			numStoreBuffers = configuration.get("mem_nr_store_buffers");
		}
		catch (NullPointerException e) {
			throw new MissingNumberOfLoadStoreBuffersException();
		}
		if (numLoadBuffers==0 || numStoreBuffers==0){
			throw new MissingNumberOfLoadStoreBuffersException();
		}
		buffers = new Buffers(numStoreBuffers, numLoadBuffers);
	}

	/**
	 * 
	 * @param configuration
	 * @throws MisssingReservationsException
	 */
	private void initializeReservationStations(Map<String, Integer> configuration)
			throws MissingNumberOfReservationStationsException {
//		reservationStationsMap = new HashMap<String, ReservastionStation>();
		int numMulRS;
		int numAddRS;
		int numAluRS;
		try{
			numMulRS = configuration.get("mul_nr_reservation");
			numAddRS = configuration.get("add_nr_reservation");
			numAluRS = configuration.get("int_nr_reservation");
			
		}catch (NullPointerException e){
			throw new MissingNumberOfReservationStationsException();
		}
		if(numMulRS==0 || numAddRS==0 || numAluRS==0){
			throw new MissingNumberOfReservationStationsException();
		}
		reservationStations = new ReservationStations(numMulRS, numAddRS, numAluRS);
	}

	/**
	 * Here we can keep executing with zero values in configuration file.
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
			FP_mult_unit = new FPMul(configuration.get("mul_delay"), cdb);
		}
		catch (NullPointerException e) {
			FP_mult_unit = new FPMul(cdb);
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
		Instruction inst = instructions_queue.peek();
		
		if (inst.OPCODE.equals(Opcode.LD) || inst.OPCODE.equals(Opcode.ST)){
						
		}
		else if (inst.OPCODE.equals(Opcode.ADD_S) || inst.OPCODE.equals(Opcode.SUB_S)){
			
		}
		else if (inst.OPCODE.equals(Opcode.MULT_S)){
			
		}
		else if (inst.OPCODE.equals(Opcode.JUMP)){
			
		}
		else if (inst.OPCODE.equals(Opcode.BNE) || inst.OPCODE.equals(Opcode.BEQ)){
			
		}
		else {
			
		}
		
		clock++;
	}
	
	//TODO 
    public void execute(){ 
    }
    
    //TODO
    public void writeback(){ 
    } 

    /**
	 * This is a test method.
     * @throws UnknownOpcodeException 
	 */
	public void printInstructions() throws UnknownOpcodeException {
		System.out.println("Input Instructions:\n");
		int j = 0;
		String str;
		
		for (Instruction inst : instructions_queue) {
			System.out.println("Instruction number " + j + ":");
			str = memory.getInst (j++);	
			inst = new Instruction(str);
		
			System.out.println("OPCODE: " + inst.OPCODE);
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

	
	
}
