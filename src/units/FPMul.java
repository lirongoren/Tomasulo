package units;

public class FPMul extends Unit {

	public FPMul(int delay) {
		super(delay);
	}

	public FPMul() {
		this (0);
	}
	
	static public float execute(float input1, float input2) {
		return input1 * input2;
	}

}
