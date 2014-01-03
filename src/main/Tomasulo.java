package main;

import java.nio.Buffer;
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
import exceptions.ProgramCounterOutOfBoundException;
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
	public Tomasulo(Memory mem, Map<String, Integer> configuration) throws MissingNumberOfReservationStationsException, MissingNumberOfLoadStoreBuffersException {

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
	 * This method creates the store & buffers. In any case of invalid input
	 * (non value / value=zero) an exception is thrown.
	 * 
	 * @param configuration
	 * @throws MissingLoadStoreBuffers
	 */
	private void initializeBuffers(Map<String, Integer> configuration) throws MissingNumberOfLoadStoreBuffersException {
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
	 * This method creates the reservation stations. In any case of invalid
	 * input (non value / value=zero) an exception is thrown.
	 * 
	 * @param configuration
	 * @throws MisssingReservationsException
	 */
	private void initializeReservationStations(Map<String, Integer> configuration) throws MissingNumberOfReservationStationsException {
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
	 * This method creates the execute units. In any case of value=0 on the
	 * cfg.txt we will create a unit with latency=0.
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
	 * This method ...
	 * 
	 * @throws UnknownOpcodeException
	 * @throws ProgramCounterOutOfBoundException
	 */
	public void step() throws UnknownOpcodeException, ProgramCounterOutOfBoundException {
		ArrayList<Instruction> tmpExecuteList = new ArrayList<Instruction>();
		ArrayList<Instruction> tmpWriteToCDBList = new ArrayList<Instruction>();
		fetchInstruction();

		if (!waitingList.isEmpty()) {
			handleWaitingList();
		}

		Instruction instruction = instructionsQueue.peek();
		if (instruction != null) {
			if (!instruction.getOPCODE().equals(Opcode.HALT)) {
				issue(tmpExecuteList);
			} else {
				instructionsQueue.poll();
			}
		}

		if (!executeList.isEmpty()) {
			execute(tmpWriteToCDBList);
		}
		if (!writeToCDBList.isEmpty()) {
			writeToCDB();
		}

		executeList.addAll(tmpExecuteList);
		writeToCDBList.addAll(tmpWriteToCDBList);

		if (waitingList.isEmpty() && instructionsQueue.isEmpty() && executeList.isEmpty() && writeToCDBList.isEmpty()) {
			globalStatus = Global.FINISHED;
		}

		System.out.println("Reservation Stations: cycle" + this.clock);
		System.out.println("Name\tOp\tVj\tVk\tQj\tQk\tA\tInstr #");
		for (LoadBuffer buffer : buffers.getLoadBuffers()) {
			if (buffer.isBusy()) {
				System.out.println(buffer.getNameOfStation() + "\t" + buffer.getOpcode().toString() + "\t" + buffer.getValue1() + "\t" + buffer.getValue2() + "\t"
						+ buffer.getFirstTag() + "\t" + buffer.getSecondTag() + "\t" + buffer.getAddress() + "\t" + buffer.getInst());
			} else {
				System.out.println(buffer.getNameOfStation());
			}
		}
		for (StoreBuffer buffer : buffers.getStoreBuffers()) {
			if (buffer.isBusy()) {
				System.out.println(buffer.getNameOfStation() + "\t" + buffer.getOpcode().toString() + "\t" + buffer.getValue1() + "\t" + buffer.getValue2() + "\t"
						+ buffer.getFirstTag() + "\t" + buffer.getSecondTag() + "\t" + buffer.getAddress() + "\t" + buffer.getInst());
			} else {
				System.out.println(buffer.getNameOfStation());
			}
		}
		for (AluReservationStation RS : reservationStations.getAluReservationStations()) {
			if (RS.isBusy()) {
				System.out.println(RS.getNameOfStation() + "\t" + RS.getOpcode().toString() + "\t" + RS.getValue1() + "\t" + RS.getValue2() + "\t" + RS.getFirstTag() + "\t"
						+ RS.getSecondTag() + "\t\t\t");
			} else {
				System.out.println(RS.getNameOfStation());
			}
		}
		for (MulOrAddReservationStation RS : reservationStations.getAddReservationStations()) {
			if (RS.isBusy()) {
				System.out.println(RS.getNameOfStation() + "\t" + RS.getOpcode().toString() + "\t" + RS.getValue1() + "\t" + RS.getValue2() + "\t" + RS.getFirstTag() + "\t"
						+ RS.getSecondTag() + "\t\t\t");
			} else {
				System.out.println(RS.getNameOfStation());
			}
		}
		for (MulOrAddReservationStation RS : reservationStations.getMulReservationStations()) {
			if (RS.isBusy()) {
				System.out.println(RS.getNameOfStation() + "\t" + RS.getOpcode().toString() + "\t" + RS.getValue1() + "\t" + RS.getValue2() + "\t" + RS.getFirstTag() + "\t"
						+ RS.getSecondTag() + "\t\t\t");
			} else {
				System.out.println(RS.getNameOfStation());
			}
		}
	}

	/**
	 * 
	 */
	private void handleWaitingList() {
		ArrayList<Instruction> tmpRemovedWaitingList = new ArrayList<Instruction>();
		for (Instruction instruction : waitingList) {
			if (instruction.isReadyToBeExecuted(reservationStations, buffers)) {
				executeList.add(instruction);
				tmpRemovedWaitingList.add(instruction);
			}
		}
		if (!tmpRemovedWaitingList.isEmpty()) {
			waitingList.removeAll(tmpRemovedWaitingList);
		}

	}

	/**
	 * Fetching an instruction from the memory to the Instruction Queue takes
	 * one clock cycle.
	 * 
	 * @return
	 * @throws UnknownOpcodeException
	 * @throws ProgramCounterOutOfBoundException
	 */
	private void fetchInstruction() throws UnknownOpcodeException, ProgramCounterOutOfBoundException {
		if (pc < 0 || pc > 1023) {
			throw new ProgramCounterOutOfBoundException();
		}
		if (pc == memory.getMaxWords() - 1) {
			System.out.println("Missing Halt Operation.\nContinue Executing Legal Instructions: ");
			fetchingStatus = Global.FINISHED;
		} else if (fetchingStatus == Global.UNFINISHED) {
			Instruction inst = new Instruction(memory.loadAsBinaryString(pc), pc++);
			if (inst.getOPCODE().equals(Opcode.HALT)) {
				fetchingStatus = Global.FINISHED;
			}
			instructionsQueue.add(inst);
			instructionsStaticQueue.add(inst);
		}
		clock++;
	}

	/**
	 * For JUMP instructions.
	 */
	private void emptyInstructionsQueue() {
		instructionsQueue.clear();
	}

	/*
	 * 1. add a list of instructions between the issue() and execute() - the
	 * instruction will be added to the waiting_list / exec_list 2. we need to
	 * check 3 things in issue: a. if there isn't a free RS, the instruction
	 * will stay in the instructions_queue. we will peek it again in the next
	 * issue cycle, after fetching one more instruction from the memory to the
	 * instructions queue. b. if there is a free RS but some of them are tags,
	 * then the instruction will be added to the waiting_list & popped out of
	 * the instructions_queue c. if there is a free RS and the values are ready
	 * then the instruction will be added to the exec_list & popped out of the
	 * instructions_queue waiting_list: instructions that wait for the operands
	 * to be value and not tag reservation stations
	 */
	public void issue(ArrayList<Instruction> tmpExecuteList) {
		Instruction instruction = instructionsQueue.peek();

		if (instruction.getIssueCycle() == -1) {
			instruction.setIssueCycle(clock);
		}

		if (instruction.getOPCODE().equals(Opcode.LD)) {
			if (buffers.isThereFreeLoadBuffer()) {
				LoadBuffer loadBuffer = buffers.getFreeLoadBuffer();
				setBufferValues(loadBuffer, instruction, tmpExecuteList);
				registers.setFloatRegisterTag(instruction.getDST(), loadBuffer.getNameOfStation());
			} else {
				// No empty buffer yet. Will try again next cycle.
			}
		}

		else if (instruction.getOPCODE().equals(Opcode.ST)) {
			if (buffers.isThereFreeStoreBuffer()) {
				StoreBuffer storeBuffer = buffers.getFreeStoreBuffer();
				setBufferValues(storeBuffer, instruction, tmpExecuteList);
				// No need to set tag of the destination register, as the
				// destination is the memory.
			} else {
				// No empty buffer yet. Will try again next cycle.
			}
		}

		else if (instruction.getOPCODE().equals(Opcode.ADD_S) || instruction.getOPCODE().equals(Opcode.SUB_S)) {
			if (reservationStations.isThereFreeAddSubRS()) {
				MulOrAddReservationStation reservationStation = reservationStations.getFreeAddReservationStation();
				setFloatReservationStationValues(reservationStation, instruction, tmpExecuteList);
			} else {
				// No empty buffer yet. Will try again next cycle.
			}
		}

		else if (instruction.getOPCODE().equals(Opcode.MULT_S)) {
			if (reservationStations.isThereFreeMulRS()) {
				MulOrAddReservationStation reservationStation = reservationStations.getFreeMulReservationStation();
				setFloatReservationStationValues(reservationStation, instruction, tmpExecuteList);
			} else {
				// No empty RS yet. Will try again next cycle.
			}
		}

		else if (instruction.getOPCODE().equals(Opcode.ADD) || instruction.getOPCODE().equals(Opcode.SUB) || instruction.getOPCODE().equals(Opcode.ADDI)
				|| instruction.getOPCODE().equals(Opcode.SUBI)) {
			if (reservationStations.isThereFreeAluRS()) {
				AluReservationStation reservationStation = reservationStations.getFreeAluReservationStation();
				setAluReservationStationValues(reservationStation, instruction, tmpExecuteList);
			} else {
				// No empty RS yet. Will try again next cycle.
			}
		}

		else if (instruction.getOPCODE().equals(Opcode.JUMP)) {
			pc = instruction.getPc() + instruction.getIMM();
			emptyInstructionsQueue();
		}

		else if (instruction.getOPCODE().equals(Opcode.BNE) || instruction.getOPCODE().equals(Opcode.BEQ)) {
			// TODO - implement
		}

	}

	/**
	 * 
	 * @param reservationStation
	 * @param instruction
	 * @param tmpExecuteList
	 */
	private void setAluReservationStationValues(AluReservationStation reservationStation, Instruction instruction, ArrayList<Instruction> tmpExecuteList) {

		reservationStation.setBusy();
		reservationStation.setOpcode(instruction.getOPCODE());
		instruction.setStation(reservationStation.getNameOfStation());
		boolean insertToWaitingList = false;

		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			reservationStation.setValue1(registers.getIntRegisterValue(instruction.getSRC0()));
		} else {
			reservationStation.setFirstTag(registers.getIntRegisterTag(instruction.getSRC0()));
			insertToWaitingList = true;
		}

		if (instruction.getOPCODE().equals(Opcode.ADD) || instruction.getOPCODE().equals(Opcode.SUB)) {
			if (registers.getIntRegisterStatus(instruction.getSRC1()) == Status.VALUE) {
				reservationStation.setValue2(registers.getIntRegisterValue(instruction.getSRC1()));
			} else {
				reservationStation.setSecondTag(registers.getIntRegisterTag(instruction.getSRC1()));
				insertToWaitingList = true;
			}
		}

		else if ((instruction.getOPCODE().equals(Opcode.ADDI) || instruction.getOPCODE().equals(Opcode.SUBI))) {
			reservationStation.setValue2(instruction.getIMM());
		}

		registers.setIntRegisterTag(instruction.getDST(), reservationStation.getNameOfStation());

		if (insertToWaitingList) {
			waitingList.add(instructionsQueue.poll());
		} else {
			tmpExecuteList.add(instructionsQueue.poll());
		}
	}

	/**
	 * 
	 * @param reservationStation
	 * @param instruction
	 * @param tmpExecuteList
	 */
	private void setFloatReservationStationValues(MulOrAddReservationStation reservationStation, Instruction instruction, ArrayList<Instruction> tmpExecuteList) {

		reservationStation.setBusy();
		reservationStation.setOpcode(instruction.getOPCODE());
		instruction.setStation(reservationStation.getNameOfStation());
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
		registers.setFloatRegisterTag(instruction.getDST(), reservationStation.getNameOfStation());

		if (insertToWaitingList) {
			waitingList.add(instructionsQueue.poll());
		} else {
			tmpExecuteList.add(instructionsQueue.poll());
		}
	}

	/**
	 * 
	 * @param buffer
	 * @param instruction
	 * @param tmpExecuteList
	 */
	private void setBufferValues(LoadStoreBuffer buffer, Instruction instruction, ArrayList<Instruction> tmpExecuteList) {
		buffer.setBusy();
		buffer.setOpcode(instruction.getOPCODE());
		buffer.setValue1(instruction.getIMM());
		instruction.setStation(buffer.getNameOfStation());

		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			buffer.setValue2(registers.getIntRegisterValue(instruction.getSRC0()));

			// We decided to calculate the effective address at the issue stage:
			buffer.calculateAddress(instruction.getIMM(), instruction.getSRC0());

			if (!buffers.isThereAddressCollision()) {
				tmpExecuteList.add(instructionsQueue.poll());
			} else {
				// TODO - handle collision of address in the buffers.
			}
		} else {
			buffer.setSecondTag(registers.getIntRegisterTag(instruction.getSRC1()));
			waitingList.add(instructionsQueue.poll());
		}

	}

	/*
	 * iterate over the execList: 1. if the instruction.exec_start == -1: update
	 * exec_start with clock, exec_end with clock + delay, run
	 * instruction.execute() and update result (find the delay from the unit it
	 * should go into according to the opcode) 2. else if exec_end == clock and
	 * if it is, update result and pop from waiting_list and add to
	 * write2CDBList
	 * 
	 * instruction.execute() checks the opcode and according to the opcode, it
	 * runs the relevant unit.execute() and its result will enter the
	 * instruction.result
	 */
	public void execute(ArrayList<Instruction> tmpWriteToCDBList) {
		int count = 0;

		while (count < executeList.size()) {
			Instruction instruction = executeList.get(count);
			if (instruction.getExecuteStartCycle() < 0) {
				/* the instruction execution hasn't started */
				instruction.setExecuteStartCycle(clock);
				instruction.setExecuteEndCycle(clock, getDelay(instruction) - 1);
			} else if (instruction.getExecuteEndCycle() == clock) {

				boolean executed = executeInstruction(instruction);

				/* the instruction execution has ended */
				// if (instruction.getOPCODE() != Opcode.ST) { // no need to
				// write to CDB in store instructions
				// }

				if (executed == true) {
					tmpWriteToCDBList.add(instruction);
					executeList.remove(instruction);
					count--;
				}
			}
			count++;
		}
	}

	private boolean executeInstruction(Instruction instruction) {
		float float_input1, float_input2;
		int int_input1, int_input2;
		switch (instruction.getOPCODE()) {
		case LD:
			LoadBuffer load_buffer = buffers.getLoadBuffer(instruction.getStation());
			instruction.setResult(memory.load(load_buffer.getAddress()));
			break;
		case ST:
			StoreBuffer store_buffer = buffers.getStoreBuffer(instruction.getStation());
			memory.store(store_buffer.getAddress(), (int) registers.getFloatRegisterValue(instruction.getSRC1()));
			break;
		case ADD:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((int) integerALU.execute(int_input1, int_input2));
			break;
		case ADDI:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = instruction.getIMM();
			instruction.setResult((int) integerALU.execute(int_input1, int_input2));
			break;
		case SUB:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = -((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((int) integerALU.execute(int_input1, int_input2));
			break;
		case SUBI:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = -instruction.getIMM();
			instruction.setResult((int) integerALU.execute(int_input1, int_input2));
			break;
		case ADD_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) FPAddSub.execute(float_input1, float_input2));
			break;
		case SUB_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = -((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) FPAddSub.execute(float_input1, float_input2));
			break;
		case MULT_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) FPAddSub.execute(float_input1, float_input2));
			break;
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}

		return true;
		// TODO - other instructions types

		// Important - store has no writeToCDB. it ends after the execute.

	}

	/**
	 * 
	 * @param instruction
	 * @return
	 */
	private int getDelay(Instruction instruction) {
		Opcode opcode = instruction.getOPCODE();

		if (opcode.equals(Opcode.LD) || opcode.equals(Opcode.ST)) {
			return load_store_unit.getDelay();
		} else if (opcode.equals(Opcode.ADD_S) || opcode.equals(Opcode.SUB_S)) {
			return FP_add_sub_unit.getDelay();
		}

		else if (opcode.equals(Opcode.MULT_S)) {
			return FP_mult_unit.getDelay();
		}

		return alu_unit.getDelay();
	}

	/*
	 * 1. iterate over the write2CDBList, for each iteration iterate over the
	 * registers, RS's and buffers and update the tags with the value of the
	 * instruction 2. iterate over the waiting_list and for each instruction
	 * whose RS / buffer is ready, add the instruction to the execList and pop
	 * it from the waitingList
	 */
	public void writeToCDB() {
		for (Instruction instruction : writeToCDBList) {
			instruction.setWriteToCDBCycle(clock);
			reservationStations.updateTags(instruction.getStation(), instruction.getResult());
			buffers.updateTags(instruction.getStation(), instruction.getResult());
			registers.updateTags(instruction.getStation(), instruction.getResult());
		}

		writeToCDBList.clear();
	}

	/**
	 * This is a test method.
	 * 
	 * @throws UnknownOpcodeException
	 * @throws ProgramCounterOutOfBoundException
	 */
	public void printInstructions() throws UnknownOpcodeException, ProgramCounterOutOfBoundException {
		System.out.println("Input Instructions:\n");
		int j = 0;
		String binStr;
		while (fetchingStatus == globalStatus) {
			fetchInstruction();
		}

		for (Instruction inst : instructionsQueue) {
			System.out.println("Instruction number " + j + ":");
			binStr = memory.loadAsBinaryString(j);

			inst = new Instruction(binStr, j++);

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
			System.out.println("Integer Register " + i + ": " + registers.getIntRegisterValue(i));
		}
		System.out.println();
		System.out.println("Float registers values:\n");
		for (int i = 0; i < 16; i++) {
			System.out.println("Float Register " + i + ": " + registers.getFloatRegisterValue(i));
		}
	}

	// Getters & Setters:
	public boolean isFinished() {
		return globalStatus;
	}

	public Registers getRegisters() {
		return this.registers;
	}

	public Queue<Instruction> getInstructionsStaticQueue() {
		return instructionsStaticQueue;
	}

}
