package main;

public class Global {

	public static final int LD = 0; //F[DST] = MEM[R[SRC0] + IMM]
	public static final int ST = 1; //MEM[R[SRC0] + IMM] = F[SRC1]
	public static final int JUMP = 2; //unconditional branch to PC + IMM
	public static final int BEQ = 3; //if R[SRC0] == R[SRC1] branch to PC + IMM
	public static final int BNE = 4; //if R[SRC0] != R[SRC1] branch to PC + IMM
	public static final int ADD = 5; //R[DST] = R[SRC0] + R[SRC1]
	public static final int ADDI = 6; //R[DST] = R[SRC0] + IMM
    public static final int SUB = 7; //R[DST] = R[SRC0] - R[SRC1]
    public static final int SUBI = 8; //R[DST] = R[SRC0] - IMM
    public static final int ADD_S = 9; //F[DST] = F[SRC0] + F[SRC1]
    public static final int SUB_S = 10; //F[DST] = F[SRC0] - F[SRC1]
    public static final int MULT_S = 11; //F[DST] = F[SRC0] * F[SRC1]
    public static final int HALT = 12; //exit simulator
    
    public static final int FloatingPointRegistersNum = 16;
    public static final int IntegerRegistersNum = 16;
         
    public static final boolean UNFINISHED = false;
    public static final boolean FINISHED = true; 
    
    public static final int IDLE = 0;
    public static final int BUSY = 1; 

}
