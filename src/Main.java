import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class Main {

	public static void main(String[] args) throws FileNotFoundException {
			
		Parser parser = new Parser();
		Tomasulo tomasulo = new Tomasulo();
		
		try {
			
			parser.parseMemoryInputFile("C:\\memin.txt");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
