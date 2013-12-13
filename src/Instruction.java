
public class Instruction {
	
	String name = "";

    int issue = 0;
    int exec = 0;
    int writeback = 0;

    int op = 0;
    int des = 0;
    int src1 = 0;
    int src2 = 0;
    int signedImm = 0;
    
    int station = 0;

    float result = 0f;

    public Instruction(String s){
            name = s;
            
            //TO DO: calculating op, des, src1, src2, signedImm variables.
    } 

}
