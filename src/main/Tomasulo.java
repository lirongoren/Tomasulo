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
import exceptions.ProgramCounterOutOfBoundException;
import exceptions.UnknownOpcodeException;

/**
 * That class is the main flow of the processor.
 */
public class Tomasulo {
	
	private Queue<Instruction> instructionsQueue;			/* All of the instructions that are in the instructions queue */
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
	private boolean fetchedHaltInst;
	private int clock;
	private int pc;

	private integerALU alu_unit;
	private FPAddSub FP_add_sub_unit;
	private FPMul FP_mult_unit;
	private LoadStore load_store_unit;

	/**
	 * Initializes the program's flow according to the configuration and memory given.
	 * @param mem the memory of the program.
	 * @param configuration the configuration of the program.
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
		fetchedHaltInst = false;	
		
		registers = new Registers();

		initializeReservationStations(configuration);
		initializeBuffers(configuration);
		initializeUnits(configuration);
	}

	/**
	 * Creates the store & load buffers. In any case of invalid input
	 * (non value / value = zero) an exception is thrown.
	 * @param configuration the configuration of the processor.
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
	 * Creates the reservation stations of the processor. In any case of invalid
	 * input (non value / value=zero) an exception is thrown.
	 * @param configuration
	 * @throws MisssingReservationsException
	 */
	private void initializeReservationStations(Map<String, Integer> configuration) throws MissingNumberOfReservationStationsException {
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
	 * This method triggers the Tomasulo algorithm for one clock cycle.
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
				fetchingStatus = Global.FINISHED;
				instruction.setIssueCycle(clock);
				instruction.setExecuteStartCycle(clock);
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

//		printReservationStations();
	}

	/**
	 * Every step of Tomasulo algorithm we call this method in order to check if
	 * some instructions can be moved from the waiting list to the executing
	 * list as the operands are ready in the appropriate reservation station.
	 */
	private void handleWaitingList() {
		ArrayList<Instruction> tmpRemovedWaitingList = new ArrayList<Instruction>();
		for (Instruction instruction : waitingList) {
			if (instruction.isReadyToBeExecuted(reservationStations, buffers)) {
				if (instruction.getOPCODE() == Opcode.LD) {
					LoadBuffer load_buffer = buffers.getLoadBuffer(instruction.getStation());
					load_buffer.calculateAddress(instruction.getIMM(), load_buffer.getValue1());
				} else if (instruction.getOPCODE() == Opcode.ST) {
					StoreBuffer store_buffer = buffers.getStoreBuffer(instruction.getStation());
					store_buffer.calculateAddress(instruction.getIMM(), store_buffer.getValue1());
				}
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
	 * @throws UnknownOpcodeException
	 * @throws ProgramCounterOutOfBoundException
	 */
	private void fetchInstruction() throws UnknownOpcodeException, ProgramCounterOutOfBoundException {
		if (fetchingStatus == Global.UNFINISHED && fetchedHaltInst==false) {
			if (pc < 0 || pc > memory.getMaxWords() - 1) {
				// We may get here as a result of invalid JUMP instruction.
				throw new ProgramCounterOutOfBoundException();
			}
			if (pc == memory.getMaxWords() - 1) {
				// We may get here if no HALT instruction was found.
				//System.out.println("Missing Halt Operation.\nContinue Executing Legal Instructions: ");
				fetchingStatus = Global.FINISHED;
			} else {
				// Legal situation - fetching an instruction from the memory:
				Instruction inst = new Instruction(memory.loadAsBinaryString(pc), pc++);
				if (inst.getOPCODE().equals(Opcode.HALT) ) {
					fetchedHaltInst=true;
					fetchingStatus = Global.FINISHED;
				}
				instructionsQueue.add(inst);
				instructionsStaticQueue.add(inst);
			}
		}
		clock++;
	}

	/**
	 * For JUMP instructions.
	 */
	private void emptyInstructionsQueue() {
		instructionsQueue.clear();
		fetchedHaltInst=false;
	}

	/**
	 * The issue method responsible for the decoding stage of the top
	 * instruction in the instruction queue each clock cycle, as long we have
	 * instruction to fetch from the memory.
	 * 
	 * We will consider the following scenarios: 1. If there isn't a free
	 * appropriate reservation station, the instruction will stay in the
	 * instructions_queue. We will peek it again in the next issue cycle, after
	 * fetching one more instruction from the memory to the instructions queue.
	 * This is what we call structural hazard..
	 * 
	 * 2. If there is a free reservation station but some of the operands are
	 * tags, then the instruction will be added to the waiting_list & popped out
	 * of the instructions_queue.
	 * 
	 * 3. if there is a free reservation station and the operands are ready
	 * values from the registers, then the instruction will be added to the
	 * execute list & popped out of the instructions_queue waiting_list.
	 */
	public void issue(ArrayList<Instruction> tmpExecuteList) {
		Instruction instruction = instructionsQueue.peek();

		instruction.setIssueCycle(clock);

		if (instruction.getOPCODE().equals(Opcode.LD) && buffers.isThereFreeLoadBuffer()) {	
			LoadBuffer loadBuffer = buffers.getFreeLoadBuffer();
			setLoadBufferValues(loadBuffer, instruction, tmpExecuteList);
			registers.setFloatRegisterTag(instruction.getDST(), loadBuffer.getNameOfStation());	
		}

		else if (instruction.getOPCODE().equals(Opcode.ST) && buffers.isThereFreeStoreBuffer()) {	
			StoreBuffer storeBuffer = buffers.getFreeStoreBuffer();
			setStoreBufferValues(storeBuffer, instruction, tmpExecuteList);
			// No need to set tag of the destination register, as the destination is the memory.
		}

		else if ((instruction.getOPCODE().equals(Opcode.ADD_S) || instruction.getOPCODE().equals(Opcode.SUB_S))
				&& reservationStations.isThereFreeAddSubRS()) {
			
			MulOrAddReservationStation reservationStation = reservationStations.getFreeAddReservationStation();
			setFloatReservationStationValues(reservationStation, instruction, tmpExecuteList);	 
		}

		else if (instruction.getOPCODE().equals(Opcode.MULT_S) && reservationStations.isThereFreeMulRS()) {
			MulOrAddReservationStation reservationStation = reservationStations.getFreeMulReservationStation();
			setFloatReservationStationValues(reservationStation, instruction, tmpExecuteList);
		}

		else if ((instruction.getOPCODE().equals(Opcode.ADD) || instruction.getOPCODE().equals(Opcode.SUB) || instruction.getOPCODE().equals(Opcode.ADDI)
				|| instruction.getOPCODE().equals(Opcode.SUBI)) && reservationStations.isThereFreeAluRS()) {
			
			AluReservationStation reservationStation = reservationStations.getFreeAluReservationStation();
			setAluReservationStationValues(reservationStation, instruction, tmpExecuteList);	
		}

		else if (instruction.getOPCODE().equals(Opcode.JUMP)) {
			pc = instruction.getPc() + instruction.getIMM();
			emptyInstructionsQueue();
		}

		else if (instruction.getOPCODE().equals(Opcode.BNE) || instruction.getOPCODE().equals(Opcode.BEQ)) {
			branchResolution(instruction);
		}

	}

	/**
	 * This method checks if the operands of the jump instruction are ready. If
	 * not - return and the jump instruction will stay at the top of the
	 * instructions queue. If yes - calculate the branch operation result and
	 * change the pc accordingly.
	 * 
	 * @param instruction
	 */
	private void branchResolution(Instruction instruction) {
		int firstValue;
		int secondValue;

		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			firstValue = registers.getIntRegisterValue(instruction.getSRC0());
		} else {
			// There is a data dependency.
			return;
		}

		if (registers.getIntRegisterStatus(instruction.getSRC1()) == Status.VALUE) {
			secondValue = registers.getIntRegisterValue(instruction.getSRC1());
		} else {
			// There is a data dependency.
			return;
		}
		instruction.setExecuteStartCycle(clock);
		if (instruction.getOPCODE().equals(Opcode.BNE) && firstValue != secondValue) {
			pc = instruction.getPc() + instruction.getIMM();
			emptyInstructionsQueue();
		} else if (instruction.getOPCODE().equals(Opcode.BEQ) && firstValue == secondValue) {
			pc = instruction.getPc() + instruction.getIMM();
			emptyInstructionsQueue();
		}
		else{
			instructionsQueue.poll();
			
		}
		fetchingStatus = Global.UNFINISHED;
	}

	/**
	 * This method fulfill the reservation station with the required values.
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
	 * This method fulfill the reservation station with the required values.
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
	 * This method fulfill the load buffer with the required values.
	 * 
	 * @param buffer
	 * @param instruction
	 * @param tmpExecuteList
	 */
	private void setLoadBufferValues(LoadStoreBuffer buffer, Instruction instruction, ArrayList<Instruction> tmpExecuteList) {
		buffer.setBusy();
		buffer.setOpcode(instruction.getOPCODE());
		instruction.setStation(buffer.getNameOfStation());

		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			buffer.setValue1(registers.getIntRegisterValue(instruction.getSRC0()));

			// We decided to calculate the effective address at the issue stage:
			buffer.calculateAddress(instruction.getIMM(), buffer.getValue1());

			if (!buffers.isThereAddressCollision()) {
				tmpExecuteList.add(instructionsQueue.poll());
			} else {
				// TODO - handle collision of address in the buffers.
			}

		} else {
			buffer.setFirstTag(registers.getIntRegisterTag(instruction.getSRC0()));
			waitingList.add(instructionsQueue.poll());
		}

	}

	/**
	 * This method fulfill the store buffer with the required values.
	 * 
	 * @param buffer
	 * @param instruction
	 * @param tmpExecuteList
	 */
	private void setStoreBufferValues(LoadStoreBuffer buffer, Instruction instruction, ArrayList<Instruction> tmpExecuteList) {
		buffer.setBusy();
		buffer.setOpcode(instruction.getOPCODE());
		instruction.setStation(buffer.getNameOfStation());

		if (registers.getIntRegisterStatus(instruction.getSRC0()) == Status.VALUE) {
			buffer.setValue1(registers.getIntRegisterValue(instruction.getSRC0()));
		} else {
			buffer.setFirstTag(registers.getIntRegisterTag(instruction.getSRC0()));
		}
		if (registers.getFloatRegisterStatus(instruction.getSRC1()) == Status.VALUE) {
			buffer.setValue2(registers.getFloatRegisterValue(instruction.getSRC1()));
		} else {
			buffer.setSecondTag(registers.getFloatRegisterTag(instruction.getSRC1()));
		}

		if (buffer.getFirstTag().isEmpty()) {

			// We decided to calculate the effective address at the issue stage:
			buffer.calculateAddress(instruction.getIMM(), buffer.getValue1());

			if (buffer.getSecondTag().isEmpty()) {
				if (!buffers.isThereAddressCollision()) {
					tmpExecuteList.add(instructionsQueue.poll());
				} else {
					// TODO - handle collision of address in the buffers.
				}

			} else {
				waitingList.add(instructionsQueue.poll());
			}

		} else {
			waitingList.add(instructionsQueue.poll());
		}

	}

	/**
	 * This method iterate over the execList:
	 * 
	 * 1. if the instruction.exec_start == -1:
	 * update exec_start & exec_end after calculating the NextAvailableCycle of the appropriate unit.
	 * 
	 * 2. else if exec_end == clock, we will call executeInstruction method,
	 * remove the instruction from the executing list and add it to the
	 * writeToCDBList.
	 */
	public void execute(ArrayList<Instruction> tmpWriteToCDBList) {
		int count = 0;

		while (count < executeList.size()) {
			Instruction instruction = executeList.get(count);
			if (instruction.getExecuteStartCycle() < 0) {
				/* the instruction execution hasn't started */
				int nextAvailableCycle = getNextAvailableCycle(instruction);
				instruction.setExecuteStartCycle(clock + nextAvailableCycle);
				instruction.setExecuteEndCycle(clock + nextAvailableCycle, getDelay(instruction) - 1);
				if (instruction.getExecuteEndCycle() == clock) {
					updateUnit(instruction);
				}
			}
			if (instruction.getExecuteEndCycle() == clock) {
				executeInstruction(instruction);
				tmpWriteToCDBList.add(instruction);
				executeList.remove(instruction);
				count--;
			}
			if (instruction.getExecuteStartCycle() + 1 == clock) {
				updateUnit(instruction);
			}

			count++;
		}
	}

	/**
	 * 
	 * @param instruction
	 * @return
	 */
	private int getNextAvailableCycle(Instruction instruction) {
		int numOfCycles = 0;
		switch (instruction.getOPCODE()) {
		case ADD:
		case ADDI:
		case SUB:
		case SUBI:
			numOfCycles = this.alu_unit.getNumOfInstructionsWaiting();
			this.alu_unit.increaseNumOfInstructionsWaiting();
			break;
		case ADD_S:
		case SUB_S:
			numOfCycles = this.FP_add_sub_unit.getNumOfInstructionsWaiting();
			this.FP_add_sub_unit.increaseNumOfInstructionsWaiting();
			break;
		case MULT_S:
			numOfCycles = this.FP_mult_unit.getNumOfInstructionsWaiting();
			this.FP_mult_unit.increaseNumOfInstructionsWaiting();
			break;
		case LD:
		case ST:
			numOfCycles = this.load_store_unit.getNumOfInstructionsWaiting();
			this.load_store_unit.increaseNumOfInstructionsWaiting();
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}
		return numOfCycles;
	}

	/**
	 * 
	 * @param instruction
	 */
	private void executeInstruction(Instruction instruction) {
		float float_input1, float_input2;
		int int_input1, int_input2;
		switch (instruction.getOPCODE()) {
		case LD:
			LoadBuffer load_buffer = buffers.getLoadBuffer(instruction.getStation());
			instruction.setResult(memory.load(load_buffer.getAddress()));
			break;
		case ST:
			StoreBuffer store_buffer = buffers.getStoreBuffer(instruction.getStation());
			memory.store(store_buffer.getAddress(), store_buffer.getValue2());
			break;
		case ADD:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((int) this.alu_unit.execute(int_input1, int_input2));
			break;
		case ADDI:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = instruction.getIMM();
			instruction.setResult((int) this.alu_unit.execute(int_input1, int_input2));
			break;
		case SUB:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = -((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((int) this.alu_unit.execute(int_input1, int_input2));
			break;
		case SUBI:
			int_input1 = ((AluReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			int_input2 = -instruction.getIMM();
			instruction.setResult((int) this.alu_unit.execute(int_input1, int_input2));
			break;
		case ADD_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) this.FP_add_sub_unit.execute(float_input1, float_input2));
			break;
		case SUB_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = -((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) this.FP_add_sub_unit.execute(float_input1, float_input2));
			break;
		case MULT_S:
			float_input1 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue1();
			float_input2 = ((MulOrAddReservationStation) reservationStations.getReservationStation(instruction.getStation())).getValue2();
			instruction.setResult((float) this.FP_mult_unit.execute(float_input1, float_input2));
			break;
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}

		// TODO - other instructions types
	}

	/**
	 * 
	 * @param instruction
	 */
	private void updateUnit(Instruction instruction) {
		switch (instruction.getOPCODE()) {
		case LD:
		case ST:
			this.load_store_unit.decreaseNumOfInstructionsWaiting();
			break;
		case ADD:
		case ADDI:
		case SUB:
		case SUBI:
			this.alu_unit.decreaseNumOfInstructionsWaiting();
			break;
		case ADD_S:
		case SUB_S:
			this.FP_add_sub_unit.decreaseNumOfInstructionsWaiting();
			break;
		case MULT_S:
			this.FP_mult_unit.decreaseNumOfInstructionsWaiting();
			break;
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}

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

	/**
	 * 1. iterate over the write2CDBList, for each iteration iterate over the
	 * registers, RS's and buffers and update the tags with the value of the
	 * instruction.
	 * 
	 * 2. iterate over the waiting_list and for each instruction
	 * whose RS / buffer is ready, add the instruction to the execList and pop
	 * it out of the waitingList.
	 */
	public void writeToCDB() {
		for (Instruction instruction : writeToCDBList) {
			if (instruction.getOPCODE() != Opcode.ST) {
				instruction.setWriteToCDBCycle(clock);
				reservationStations.updateTags(instruction.getStation(), instruction.getResult());
				buffers.updateTags(instruction.getStation(), instruction.getResult());
				registers.updateTags(instruction.getStation(), instruction.getResult());
			}
			instruction.freeStation(reservationStations, buffers);
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
		System.out.println("Input Instructions:");
		System.out.println("inst#\t|OPCODE\t|DST\t|SRC0\t|SRC1\t|IMM");
		int j = 0;
		String binStr;
		while (fetchingStatus == globalStatus) {
			fetchInstruction();
		}

		for (Instruction inst : instructionsQueue) {
			binStr = memory.loadAsBinaryString(j);
			inst = new Instruction(binStr, j++);
			System.out.println(j + "\t|" + inst.getOPCODE() + "\t|" + inst.getDST() + "\t|" + inst.getSRC0() + "\t|" + inst.getSRC1() + "\t|" + inst.getIMM());
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

	/**
	 * This is a test method.
	 */
	@SuppressWarnings("unused")
	private void printReservationStations() {
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
