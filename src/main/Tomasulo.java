package main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import main.Instruction.Opcode;
import registers.Register.Status;
import registers.Registers;
import reservationStations.MulOrAddReservationStation;
import reservationStations.ReservationStations;
import units.*;
import buffers.*;

import cdb.CDB;
import exceptions.*;

public class Tomasulo {

	// TODO: add a static arrayList<Instruction> that will be sent later to the trace output file
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
			throws MissingNumberOfReservationStationsException,
			MissingNumberOfLoadStoreBuffersException {
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
		if (!inst.OPCODE.equals(Opcode.HALT) && pc < memory.getMaxWords() - 1) {
			instructions_queue.add(inst);

			issue();
			execute();
			writeToCDB();
		}

		else if (inst.OPCODE.equals(Opcode.HALT)) {
			System.out.println("Instruction number " + pc
					+ " is an HALT Operation");
			status = Global.FINISHED;
		}

		else if (pc == memory.getMaxWords() - 1) {
			System.out
					.println("Missing Halt Operation.\nContinue Executing Legal Instructions: ");
			status = Global.FINISHED;
		}
	}

	/**
	 * Fetching an instruction from the memory to the Instruction Queue takes
	 * one clock cycle.
	 * 
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
	private void emptyInstructionsQueue() {
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
		reservationStations = new ReservationStations(numMulRS, numAddRS,
				numAluRS);
	}

	/**
	 * Here we can keep executing with zero values in configuration file.
	 * 
	 * @param configuration
	 */
	private void initializeUnits(Map<String, Integer> configuration) {
		try {
			alu_unit = new integerALU(configuration.get("int_delay"), cdb);
		} catch (NullPointerException e) {
			alu_unit = new integerALU(cdb);
		}

		try {
			FP_add_sub_unit = new FPAddSub(configuration.get("add_delay"), cdb);
		} catch (NullPointerException e) {
			FP_add_sub_unit = new FPAddSub(cdb);
		}

		try {
			FP_mult_unit = new FPMul(configuration.get("mul_delay"), cdb);
		} catch (NullPointerException e) {
			FP_mult_unit = new FPMul(cdb);
		}

		try {
			load_store_unit = new LoadStore(configuration.get("mem_delay"), cdb);
		} catch (NullPointerException e) {
			load_store_unit = new LoadStore(cdb);
		}
	}

	/* TODO:
	 * 1. add a list of instructions between the issue() and execute() - the instruction will be added to the
	 * waiting_list / exec_list
	 * 2. we need to check 3 things in issue:
	 * a. if there isn't a free RS, the instruction will stay in the instructions_queue.
	 * b. if there is a free RS but some of them are tags, then the instruction will be added to the waiting_list &
	 * popped out of the instructions_queue
	 * c. if there is a free RS and the values are ready then the instruction will be added to the exec_list &
	 * popped out of the instructions_queue
	 * waiting_list: instructions that wait for the operands to be value and not tag reservation stations
	 *  
	 */
	
	
	// TODO
	public void issue() {
		Instruction instruction = instructions_queue.peek();
		instruction.issue = pc;
		if (instruction.OPCODE.equals(Opcode.LD) || instruction.OPCODE.equals(Opcode.ST)) {

		} else if (instruction.OPCODE.equals(Opcode.ADD_S)
				|| instruction.OPCODE.equals(Opcode.SUB_S)) {
			if (reservationStations.isThereFreeAddRS()) {
				MulOrAddReservationStation reservationStation = reservationStations
						.getFreeAddReservationStation();
				reservationStation.setBusy();
				reservationStation.setOpcode(instruction.OPCODE);
				if (registers.getFloatRegisterStatus(instruction.SRC0) == Status.VALUE) {
					reservationStation.setValue1(registers
							.getFloatRegisterValue(instruction.SRC0));
				} else {
					reservationStation.setFirstTag(registers
							.getFloatRegisterTag(instruction.SRC0));
				}
				if (registers.getFloatRegisterStatus(instruction.SRC1) == Status.VALUE) {
					reservationStation.setValue1(registers
							.getFloatRegisterValue(instruction.SRC1));
				} else {
					reservationStation.setFirstTag(registers
							.getFloatRegisterTag(instruction.SRC1));
				}
				registers.setFloatRegisterTag(instruction.DST,
						reservationStation.getNameOfStation());
			}
		} else if (instruction.OPCODE.equals(Opcode.MULT_S)) {
			if (reservationStations.isThereFreeMulRS()) {
				MulOrAddReservationStation reservationStation = reservationStations
						.getFreeMulReservationStation();
				reservationStation.setBusy();
				reservationStation.setOpcode(instruction.OPCODE);
				if (registers.getFloatRegisterStatus(instruction.SRC0) == Status.VALUE) {
					reservationStation.setValue1(registers
							.getFloatRegisterValue(instruction.SRC0));
				} else {
					reservationStation.setFirstTag(registers
							.getFloatRegisterTag(instruction.SRC0));
				}
				if (registers.getFloatRegisterStatus(instruction.SRC1) == Status.VALUE) {
					reservationStation.setValue1(registers
							.getFloatRegisterValue(instruction.SRC1));
				} else {
					reservationStation.setFirstTag(registers
							.getFloatRegisterTag(instruction.SRC1));
				}
				registers.setFloatRegisterTag(instruction.DST,
						reservationStation.getNameOfStation());
			}
		} else if (instruction.OPCODE.equals(Opcode.JUMP)) {
			// TODO - implement
		} else if (instruction.OPCODE.equals(Opcode.BNE)
				|| instruction.OPCODE.equals(Opcode.BEQ)) {
			// TODO - implement
		} else {
			// TODO - implement
		}

		clock++;
	}

	/* TODO:
	 * iterate over the execList:
	 * 1. if the instruction.exec_start == -1: update exec_start with clock, exec_end with clock + delay,
	 * run instruction.execute() and update result (find the delay from the unit it should go into
	 * according to the opcode
	 * 2. else if exec_end == clock and if it is, update result and pop from waiting_list and
	 * add to write2CDBList
	 * 
	 * instruction.execute() checks the opcode and according to the opcode, it runs the relevant unit.execute()
	 * and its result will enter the instruction.result
	 */
	// TODO
	public void execute() {
		Instruction instruction = instructions_queue.peek();
		instruction.exec = pc;
		int int_result, memory_location;
		float float_result;
		switch (instruction.OPCODE) {
		case LD:
			memory_location = registers.getIntRegisterValue(instruction.SRC0) + instruction.IMM;
			registers.setFloatRegisterValue(instruction.DST, memory.load(memory_location));
			break;
		case ST:
			memory_location = registers.getIntRegisterValue(instruction.SRC0) + instruction.IMM;
			memory.store(memory_location, (int) registers.getFloatRegisterValue(instruction.SRC1));
			break;
		case JUMP:
			// TODO - implement
			break;
		case BEQ:
			// TODO - implement
			break;
		case BNE:
			// TODO - implement
			break;
		case ADD:
			int_result = registers.getIntRegisterValue(instruction.SRC0)
					+ registers.getIntRegisterValue(instruction.SRC1);
			registers.setIntRegisterValue(instruction.DST, int_result);
			break;
		case ADDI:
			int_result = registers.getIntRegisterValue(instruction.SRC0)
					+ instruction.IMM;
			registers.setIntRegisterValue(instruction.DST, int_result);
			break;
		case SUB:
			int_result = registers.getIntRegisterValue(instruction.SRC0)
					- registers.getIntRegisterValue(instruction.SRC1);
			registers.setIntRegisterValue(instruction.DST, int_result);
			break;
		case SUBI:
			int_result = registers.getIntRegisterValue(instruction.SRC0)
					- instruction.IMM;
			registers.setIntRegisterValue(instruction.DST, int_result);
			break;
		case ADD_S:
			float_result = registers.getFloatRegisterValue(instruction.SRC0)
					+ registers.getFloatRegisterValue(instruction.SRC1);
			registers.setFloatRegisterValue(instruction.DST, float_result);
			break;
		case SUB_S:
			float_result = registers.getFloatRegisterValue(instruction.SRC0)
					- registers.getFloatRegisterValue(instruction.SRC1);
			registers.setFloatRegisterValue(instruction.DST, float_result);
			break;
		case MULT_S:
			float_result = registers.getFloatRegisterValue(instruction.SRC0)
					* registers.getFloatRegisterValue(instruction.SRC1);
			registers.setFloatRegisterValue(instruction.DST, float_result);
			break;
		case HALT:
			// TODO - implement
			break;
		default:
			break;
		}
	}

	/* TODO
	 * 1. iterate over the write2CDBList, for each iteration iterate over the registers, RS's and buffers and update
	 * the tags with the value of the instruction
	 * 2. iterate over the waiting_list and for each instruction whose RS / buffer is ready, add the instruction
	 * to the execList and pop it from the waitingList
	 */
	// TODO
	public void writeToCDB() {
		Instruction instruction = instructions_queue.poll();
		instruction.writeback = pc;
	}

	/**
	 * This is a test method.
	 * 
	 * @throws UnknownOpcodeException
	 */
	public void printInstructions() throws UnknownOpcodeException {
		System.out.println("Input Instructions:\n");
		int j = 0;
		String str;

		for (Instruction inst : instructions_queue) {
			System.out.println("Instruction number " + j + ":");
			str = memory.getInst(j++);
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
		return status;
	}

	public Registers getRegisters() {
		return this.registers;
	}

}
