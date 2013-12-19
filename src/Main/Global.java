package Main;

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
    
    public static final int FloatingPointRegistersNum = 8;
    public static final int F0 = 0;
    public static final int F1 = 1;
    public static final int F2 = 2;
    public static final int F3 = 3;
    public static final int F4 = 4;
    public static final int F5 = 5;
    public static final int F6 = 6;
    public static final int F7 = 7; 
    public static final int F8 = 8; 
    public static final int F9 = 9; 
    public static final int F10 = 10; 
    public static final int F11 = 11; 
    public static final int F12 = 12; 
    public static final int F13 = 13; 
    public static final int F14 = 14; 
    public static final int F15 = 15; 

    public static final int IntegerRegistersNum = 8;
    public static final int R0 = 0;
    public static final int R1 = 1;
    public static final int R2 = 2;
    public static final int R3 = 3;
    public static final int R4 = 4;
    public static final int R5 = 5;
    public static final int R6 = 6;
    public static final int R7 = 7; 
    public static final int R8 = 8; 
    public static final int R9 = 9; 
    public static final int R10 = 10; 
    public static final int R11 = 11; 
    public static final int R12 = 12; 
    public static final int R13 = 13; 
    public static final int R14 = 14; 
    public static final int R15 = 15; 
        
    public static final boolean UNFINISHED = false;
    public static final boolean FINISHED = true; 
  	
}
