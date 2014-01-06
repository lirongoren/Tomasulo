package main;

import reservationStations.ReservationStations;
import buffers.Buffers;
import exceptions.UnknownOpcodeException;

/**
 * A class that represents an instruction of the program.
 */
public class Instruction {

	private String binaryInst = "";
	private String station = "";

	private int pc = -1; // TODO - check if we need that field
	private int issueCycle = -1;
	private int executeStartCycle = -1;
	private int executeEndCycle = -1;
	private int writeToCDBCycle = -1;

	private Opcode OPCODE = Opcode.LD;
	private int DST = 0;
	private int SRC0 = 0;
	private int SRC1 = 0;
	private int IMM = 0;

	Object result = null; /* Integer or Float */

	/**
	 * Initializes the instruction according to its binary representation.
	 * @param binaryInst
	 * @param pc
	 * @throws UnknownOpcodeException
	 */
	public Instruction(String binaryInst, int pc) throws UnknownOpcodeException {
		this.binaryInst = binaryInst;
		this.pc = pc;
		OPCODE = createOpcode(getOpcodeValue());
		DST = getDestinationValue();
		SRC0 = getFirstSourceValue();
		SRC1 = getSecondSourceValue();
		IMM = getImmValue();
	}

	/**
	 * Frees the instruction's relevant reservation station / buffer.
	 * @param reservationStations all of the reservation stations of the processor.
	 * @param buffers all of the buffers of the processor.
	 */
	public void freeStation(ReservationStations reservationStations, Buffers buffers) {
		switch (OPCODE) {
		case LD:
			buffers.getLoadBuffer(station).free();
			break;
		case ST:
			buffers.getStoreBuffer(station).free();
			break;
		case ADD:
		case ADDI:
		case SUB:
		case SUBI:
		case ADD_S:
		case SUB_S:
		case MULT_S:
			reservationStations.getReservationStation(station).free();
			break;
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}
	}

	/**
	 * Indicated whether the instruction is ready to go from the issue stage to the execute stage:
	 * goes to the instruction's relevant reservation station / buffer and checks if all of the
	 * values are updated (and not dependent on tags).
	 * @param reservationStations all of the reservation stations of the processor.
	 * @param buffers all of the buffers of the processor.
	 * @return true if the instruction is ready to be executed.
	 */
	public boolean isReadyToBeExecuted(ReservationStations reservationStations, Buffers buffers) {
		switch (OPCODE) {
		case LD:
			return buffers.getLoadBuffer(station).isReady();
		case ST:
			return buffers.getStoreBuffer(station).isReady();
		case ADD:
		case ADDI:
		case SUB:
		case SUBI:
		case ADD_S:
		case SUB_S:
		case MULT_S:
			return reservationStations.getReservationStation(station).isReady();
		case JUMP:
		case BEQ:
		case BNE:
		case HALT:
		default:
			break;
		}
		return false;
	}
	
	/**
	 * Creates the opcode of the instruction.
	 * @param opcode the int the represents the opcode.
	 * @return the instruction's opcode enum.
	 * @throws UnknownOpcodeException
	 */
	private Opcode createOpcode(int opcode) throws UnknownOpcodeException {
		switch (opcode) {
		case 0:
			return Opcode.LD;
		case 1:
			return Opcode.ST;
		case 2:
			return Opcode.JUMP;
		case 3:
			return Opcode.BEQ;
		case 4:
			return Opcode.BNE;
		case 5:
			return Opcode.ADD;
		case 6:
			return Opcode.ADDI;
		case 7:
			return Opcode.SUB;
		case 8:
			return Opcode.SUBI;
		case 9:
			return Opcode.ADD_S;
		case 10:
			return Opcode.SUB_S;
		case 11:
			return Opcode.MULT_S;
		case 12:
			return Opcode.HALT;
		default:
			throw new UnknownOpcodeException();
		}
	}

	/*************************************** Opcode enum *************************************/
	
	public enum Opcode {
		LD(0), ST(1), JUMP(2), BEQ(3), BNE(4), ADD(5), ADDI(6), SUB(7), SUBI(8), ADD_S(9), SUB_S(10), MULT_S(11), HALT(12);

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

	/*************************************** Getters & Setters *************************************/

	public int getPc() {
		return pc;
	}

	public void setPc(int pc) {
		this.pc = pc;
	}

	public String getBinaryInst() {
		return binaryInst;
	}

	public void setBinaryInst(String binaryInst) {
		this.binaryInst = binaryInst;
	}

	public int getIssueCycle() {
		return issueCycle;
	}

	public void setIssueCycle(int issueCycle) {
		this.issueCycle = issueCycle;
	}

	public int getExecuteStartCycle() {
		return executeStartCycle;
	}

	public void setExecuteStartCycle(int executeStartCycle) {
		this.executeStartCycle = executeStartCycle;
	}

	public int getExecuteEndCycle() {
		return executeEndCycle;
	}

	public void setExecuteEndCycle(int clock, int delay) {
		this.executeEndCycle = clock + delay;
	}

	public int getWriteToCDBCycle() {
		return writeToCDBCycle;
	}

	public void setWriteToCDBCycle(int writeTocdbCycle) {
		writeToCDBCycle = writeTocdbCycle;
	}

	public Opcode getOPCODE() {
		return OPCODE;
	}

	public void setOPCODE(Opcode oPCODE) {
		OPCODE = oPCODE;
	}

	public int getDST() {
		return DST;
	}

	public void setDST(int dST) {
		DST = dST;
	}

	public int getSRC0() {
		return SRC0;
	}

	public void setSRC0(int sRC0) {
		SRC0 = sRC0;
	}

	public int getSRC1() {
		return SRC1;
	}

	public void setSRC1(int sRC1) {
		SRC1 = sRC1;
	}

	public int getIMM() {
		return IMM;
	}

	public void setIMM(int iMM) {
		IMM = iMM;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getSecondSourceValue() {
		return Integer.parseInt(binaryInst.substring(12, 16), 2);
	}

	public int getFirstSourceValue() {
		return Integer.parseInt(binaryInst.substring(8, 12), 2);
	}

	public int getDestinationValue() {
		return Integer.parseInt(binaryInst.substring(4, 8), 2);
	}

	public int getOpcodeValue() {
		return Integer.parseInt(binaryInst.substring(0, 4), 2);
	}

	public int getImmValue() {
		int sign = Integer.parseInt(binaryInst.substring(16, 17), 2);
		int num = Integer.parseInt(binaryInst.substring(17, 32), 2);
		return (-1 * sign * ((int) Math.pow(2, 15))) + num;
	}
}
