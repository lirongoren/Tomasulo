import java.util.LinkedList;
import java.util.Queue;


public class Tomasulo {
	  Queue<Instruction> instructionQueue;
	  
	  int pc;
	  
	  public Tomasulo(){
		  instructionQueue = new LinkedList<Instruction>();
		  
		  pc =0;
	  }
}
