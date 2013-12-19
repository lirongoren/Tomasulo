import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Main {

	public static void main(String[] args) throws FileNotFoundException {

		Parser parser = new Parser(args[0], args[1]);
		Instruction instruction = new Instruction(parser.memory.get(0));
		Tomasulo tomasulo = new Tomasulo();
		for (String instruction_str : parser.memory) {
			instruction = new Instruction(instruction_str);
			tomasulo.instructions_queue.add(instruction);
		}
		tomasulo.printInstructions();

		// try {
		//
		// parser.parseMemoryInputFile("C:\\memin.txt");
		//
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}
