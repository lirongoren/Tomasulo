package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import main.Instruction.Opcode;
import registers.Register.Status;
import registers.Registers;
import reservationStations.*;
import units.FPAddSub;
import units.FPMul;
import units.LoadStore;
import units.integerALU;
import buffers.Buffers;
import buffers.LoadBuffer;
import buffers.LoadStoreBuffer;
import buffers.StoreBuffer;
import exceptions.MissingNumberOfLoadStoreBuffersException;
import exceptions.MissingNumberOfReservationStationsException;
import exceptions.UnknownOpcodeException;

public class Tomasulo {

	private Queue<Instruction> instructionsQueue;
	private Queue<Instruction> instructionsStaticQueue;
	
	private ArrayList<Instruction> waitingList;
	private ArrayList<Instruction> executeList;
	private ArrayList<Instruction> writeToCDBList;

	private Memory memory;
	private Registers registers;
	private ReservationStations reservationStations;
	private Buffers buffers;

	private boolean fetchingStatus;
	private boolean globalStatus;
	private int clock;
	private int pc;

	private integerALU alu_unit;
	private FPAddSub FP_add_sub_unit;
	private FPMul FP_mult_unit;
	private LoadStore load_store_unit;

	/**
	 * 
	 * @param mem
	 * @param configuration
	 * @throws MissingNumberOfReservationStationsException
	 * @throws MissingNumberOfLoadStoreBuffersException
	 */
	public Tomasulo(Memory mem, Map<String, Integer> configuration)
			throws MissingNumberOfReservationStationsException,
			MissingNumberOfLoadStoreBuffersException {
		
		instructionsQueue = new LinkedList<Instruction>();
		instructionsStaticQueue = new LinkedList<Instruction>();
		waitingList = new ArrayList<Instruction>();
		executeList = new ArrayList<Instruction>();
		writeToCDBList = new ArrayList<Instruction>();

		memory = mem;
		pc = 0;
		clock = 0;
		fetchingStatus = Global.UNFINISHED;
		globalStatus = Global.UNFINISHED;

		registers = new Registers();

		initializeReservationStations(configuration);
		initializeBuffers(configuration);
		initializeUnits(configuration);
	}
	
	/**
	 * 
	 * @param configuration
	 * @throws MissingLoadStoreBuffers
	 */
	private void initializeBuffers(Map<String, Integer> configuration)throws MissingNumberOfLoadStoreBuffersException {
		int numLoadBuffers;
		int numStoreBuffers;
		try {
			numLoadBuffers = configuration.get("mem_nr_load_buffers");
			numStoreBuffers = configuration.get("mem_nr_store_buffers");
		} catch (NullPointerException e) {
			throw new MissingNumberOfLoadStoreBuffersException();
		}
		if (numLoadBuffers == 0 || numStoreBuffers == 0) {
			throw new MissingNumberOfLoadStoreBuffersException();
		}
		buffers = new Buffers(numStoreBuffers, numLoadBuffers);
	}

	/**
	 * 
	 * @param configuration
	 * @throws MisssingReservationsException
	 */
	private void initializeReservationStations(
			Map<String, Integer> configuration)
			throws MissingNumberOfReservationStationsException {
		// reservationStationsMap = new HashMap<String, ReservastionStation>();
		int numMulRS;
		int numAddRS;
		int numAluRS;
		try {
			numMulRS = configuration.get("mul_nr_reservation");
			numAddRS = configuration.get("add_nr_reservation");
			numAluRS = configuration.get("int_nr_reservation");

		} catch (NullPointerException e) {
			throw new MissingNumberOfReservationStationsException();
		}
		if (numMulRS == 0 || numAddRS == 0 || numAluRS == 0) {
			throw new MissingNumberOfReservationStationsException();
		}
		reservationStations = new ReservationStations(numMulRS, numAddRS, numAluRS);
	}

	/**
	 * Here we can keep executing with zero values in configuration file.
	 * 
	 * @param configuration
	 */
	private void initializeUnits(Map<String, Integer> configuration) {
		try {
			alu_unit = new integerALU(configuration.get("int_delay"));
		} catch (NullPointerException e) {
			alu_unit = new integerALU();
		}

		try {
			FP_add_sub_unit = new FPAddSub(configuration.get("add_delay"));
		} catch (NullPointerException e) {
			FP_add_sub_unit = new FPAddSub();
		}

		try {
			FP_mult_unit = new FPMul(configuration.get("mul_delay"));
		} catch (NullPointerException e) {
			FP_mult_unit = new FPMul();
		}

		try {
			load_store_unit = new LoadStore(configuration.get("mem_delay"));
		} catch (NullPointerException e) {
			load_store_unit = new LoadStore();
		}
	}


	/**
	 * 
	 * @throws UnknownOpcodeException
	 */
	public void step() throws UnknownOpcodeException {
		fetchInstruction();
		Instruction instruction = instructionsQueue.peek();
		if (instruction != null){
			if (!instruction.getOPCODE().equals(Opcode.HALT)) {		
				issue();
				if (!executeList.isEmpty()){
					execute();
				}
				if (!writeToCDBList.isEmpty()){
					writeToCDB();
				}
			}
	
			else if (instruction.getOPCODE().equals(Opcode.HALT)) {
				System.out.println("Instruction number " + pc + " is an HALT Operation");
				instructionsQueue.poll();
				fetchingStatus = Global.FINISHED;
			}
		}
		else{
			//No more instructions to issue:
			
			if (!executeList.isEmpty()){
				execute();
			}
			if (!writeToCDBList.isEmpty()){
				writeToCDB();
			}
			if (executeList.isEmpty() && writeToCDBList.isEmpty()){
				globalStatus = Global.FINISHED;
			}
		}
	}

	/**
	 * Fetching an instruction from the memory to the Instruction Queue takes
	 * one clock cycle.
	 * 
	 * @return
	 * @throws UnknownOpcodeException
	 */
	private void fetchInstruction() throws UnknownOpcodeException {
		
		if (pc == memory.getMaxWords() - 1) {
			System.out.println("Missing Halt Operation.\nContinue Executing Legal Instructions: ");
			fetchingStatus = Global.FINISHED;
		}
		else{
			Instruction inst = new Instruction(memory.loadAsBinaryString(pc++));
			instructionsQueue.add(inst);
			instructionsStaticQueue.add(inst);
			clock++;
		}
	}

	/**
	 * For JUMP instructions.
	 */
	@SuppressWarnings("unused")
	private void emptyInstructionsQueue() {
		instructionsQueue.clear();
	}

	
	/* 
	 * 1. add a list of instructions between the issue() and execute() - the instruction will be added to the
	 * waiting_list / exec_list
	 * 2. we need to check 3 things in issue:
	 * a. if there isn't a free RS, the instruction will stay in the instructions_queue.
	 *    we will peek it again in the next issue cycle, after fetching one more instruction from the memory
	 *    to the instructions queue.
	 * b. if there is a free RS but some of them are tags, then the instruction will be added to the waiting_list &
	 * popped out of the instructions_queue
	 * c. if there is a free RS and the values are ready then the instruction will be added to the exec_list &
	 * popped out of the instructions_queue
	 * waiting_list: instructions that wait for the operands to be value and not tag reservation stations
	 *  
	 */
	public void issue() {
		Instruction instruction = instructionsQueue.peek();
				
		if (instruction.getIssueCycle() == -1){
			instruction.setIssueCycle(clock);
		}
		
		if (instruction.getOPCODE().equals(Opcode.LD)) {
			if (buffers.isThereFreeLoadBuffer()) {
				LoadBuffer loadBuffer = buffers.getFreeLoadBuffer();
				setBufferValues(loadBuffer, instruction);			
				registers.setFloatRegisterTag(instruction.getDST(),	loadBuffer.getNameOfStation());
			}
			else{
				//No empty buffer yet. Will try again next cycle.
			}
		} 
		
		else if (instruction.getOPCODE().equals(Opcode.ST)) {
			if (buffers.isThereFreeStoreBuffer()) {
				StoreBuffer storeBuffer = buffers.getFreeStoreBuffer();
				setBufferValues(storeBuffer, instruction);
			}
			else{
				//No empty buffer yet. Will try again next cycle.
			}
		} 
		
		else if (instruction.getOPCODE().equals(Opcode.ADD_S) || instruction.getOPCODE().equals(Opcode.SUB_S)) {
			if (reservationStations.isThereFreeAddSubRS()) {
				MulOrAddReservationStation reservationStation = reservationStations.getFreeAddReservationStation();
				setFloatReservationStationValues(reservationStation, instruction);
			}
			else{
				//No empty buffer yet. Will try again next cycle.
			}
		} 
		
		else if (instruction.getOPCODE().equals(Opcode.MULT_S)) {
			if (reservationStations.isThereFreeMulRS()) {
				MulOrAddReservationStation reservationStation = reservationStations.getFreeMulReservationStation();
				setFloatReservationStationValues(reservationStation, instruction);		
			}
		} 
		
		else if (instruction.getOPCODE().equals(Opcode.ADD) || instruction.getOPCODE().equals(Opcode.SUB)) {
			// TODO - implement
		} 
		
		else if(instruction.getOPCODE().equals(Opcode.ADDI) || instruction.getOPCODE().equals(Opcode.SUBI)){
			
		}
		
		else if (instruction.getOPCODE().equals(Opcode.JUMP)) {
			// TODO - implement
		} 
		
		else if (instruction.getOPCODE().equals(Opcode.BNE)	|| instruction.getOPCODE().equals(Opcode.BEQ)) {
			// TODO - implement
		}
		
		else {
			// TODO - implement
		}

		
	}
	
	/**
	 * 
	 * @param reservationStation
	 * @param instruction
	 */
	private void setFloatReservationStationValues(MulOrAddReservationStation reservationStation,	Instruction instruction) {
		reservationStation.setBusy();
		reservationStation.setOpcode(instruction.getOPCODE());
		boolean insertToWaitingList = false;
		
		if (registers.getFloatRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			reservationStation.setValue1(registers.getFloatRegisterValue(instruction.getSRC0()));
		} else {
			reservationStation.setFirstTag(registers.getFloatRegisterTag(instruction.getSRC0()));
			insertToWaitingList = true;
		}
		if (registers.getFloatRegisterStatus(instruction.getSRC1()) == Status.VALUE) {
			reservationStation.setValue2(registers.getFloatRegisterValue(instruction.getSRC1()));
		} else {
			reservationStation.setSecondTag(registers.getFloatRegisterTag(instruction.getSRC1()));
			insertToWaitingList = true;
		}
		registers.setFloatRegisterTag(instruction.getDST(),	reservationStation.getNameOfStation());
		
		if (insertToWaitingList){
			waitingList.add(instructionsQueue.poll());
		}
		else{
			executeList.add(instructionsQueue.poll());
		}
	}

	/**
	 * 
	 * @param loadBuffer
	 * @param instruction
	 */
	private void setBufferValues(LoadStoreBuffer loadBuffer, Instruction instruction) {
		loadBuffer.setBusy();
		loadBuffer.setOpcode(instruction.getOPCODE());
		loadBuffer.setValue1(instruction.getIMM());
		
		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			loadBuffer.setValue2(registers.getIntRegisterValue(instruction.getSRC0()));
			executeList.add(instructionsQueue.poll());
		}
		else{
			loadBuffer.setSecondTag(registers.getIntRegisterTag(instruction.getSRC1()));
			waitingList.add(instructionsQueue.poll());
		}
	}

	/* 
	 * iterate over the execList:
	 * 1. if the instruction.exec_start == -1: update exec_start with clock, exec_end with clock + delay,
	 * run instruction.execute() and update result (find the delay from the unit it should go into
	 * according to the opcode)
	 * 2. else if exec_end == clock and if it is, update result and pop from waiting_list and
	 * add to write2CDBList
	 * 
	 * instruction.execute() checks the opcode and according to the opcode, it runs the relevant unit.execute()
	 * and its result will enter the instruction.result
	 */
	public void execute() {
		clock++;
		for (Instruction instruction : executeList) {
			if (instruction.getExecuteStartCycle() < 0) { 
				/* the instruction execution hasn't started */
				instruction.setExecuteStartCycle(clock);
				instruction.setExecuteEndCycle(clock, getDelay(instruction));
				executeInstruction(instruction);
			}
			else if (instruction.getExecuteEndCycle() == clock) { 
				/* the instruction execution has ended */
				writeToCDBList.add(instruction);
//				executeList.remove(instruction);
			}
		}
	}
	
	private void executeInstruction(Instruction instruction) {
		if (instruction.getExecuteEndCycle() == clock){
			
			if (instruction.getOPCODE().equals(Opcode.LD)){
				LoadBuffer loadBuffer = buffers.getLoadBuffer(instruction.getStation());
				loadBuffer.calculateAddress(instruction.getIMM(), instruction.getSRC0());
			}
			
			//TODO - other instructions types
			
			
		}
	
	}

	/**
	 * 
	 * @param instruction
	 * @return
	 */
	private int getDelay(Instruction instruction) {
		Opcode opcode = instruction.getOPCODE();
		
		if (opcode.equals(Opcode.LD) || opcode.equals(Opcode.ST)){
			return load_store_unit.getDelay();
		}
		else if (opcode.equals(Opcode.ADD_S) || opcode.equals(Opcode.SUB_S)){
			return FP_add_sub_unit.getDelay();
		}
		
		else if (opcode.equals(Opcode.MULT_S)){
			return FP_mult_unit.getDelay();
		}
		
		return alu_unit.getDelay();
	}

	/* 
	 * 1. iterate over the write2CDBList, for each iteration iterate over the registers, RS's and buffers and update
	 * the tags with the value of the instruction
	 * 2. iterate over the waiting_list and for each instruction whose RS / buffer is ready, add the instruction
	 * to the execList and pop it from the waitingList
	 */
	public void writeToCDB() {
		for (Instruction instruction : writeToCDBList) {
			instruction.setWriteToCDBCycle(clock);
			reservationStations.updateTags(instruction.getStation(), instruction.getResult());
			buffers.updateTags(instruction.getStation(), instruction.getResult());
			registers.updateTags(instruction.getStation(), instruction.getResult());
		}
		for (Instruction instruction : waitingList) {
			if (instruction.isReadyToBeExecuted(reservationStations, buffers)) {
				executeList.add(instruction);
				waitingList.remove(instruction);
			}
		}
	}

	/**
	 * This is a test method.
	 * 
	 * @throws UnknownOpcodeException
	 */
	public void printInstructions() throws UnknownOpcodeException {
		System.out.println("Input Instructions:\n");
		int j = 0;
		String binStr;

		for (Instruction inst : instructionsQueue) {
			System.out.println("Instruction number " + j + ":");
			binStr = memory.loadAsBinaryString(j++);
			
			inst = new Instruction(binStr);

			System.out.println("OPCODE: " + inst.getOPCODE());
			System.out.println("DST: " + inst.getDST());
			System.out.println("SRC0: " + inst.getSRC0());
			System.out.println("SRC1: " + inst.getSRC1());
			System.out.println("IMM: " + inst.getIMM());
			System.out.println();
		}
	}

	/**
	 * This is a test method.
	 */
	public void printRegistersValues() {
		System.out.println("Integer registers values:\n");
		for (int i = 0; i < 16; i++) {
			System.out.println("Integer Register " + i + ": "
					+ registers.getIntRegisterValue(i));
		}
		System.out.println();
		System.out.println("Float registers values:\n");
		for (int i = 0; i < 16; i++) {
			System.out.println("Float Register " + i + ": "
					+ registers.getFloatRegisterValue(i));
		}
	}

	// Getters & Setters:
	public boolean isFinished() {
		return fetchingStatus;
	}

	public Registers getRegisters() {
		return this.registers;
	}
	
	public Queue<Instruction> getInstructionsStaticQueue() {
		return instructionsStaticQueue;
	}

}
