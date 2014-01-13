package units;

public class integerALU extends Unit {

	public integerALU(int delay) {
		super(delay);
	}
	
	public int execute(int input1, int input2) {
		return input1 + input2;
	}

}
