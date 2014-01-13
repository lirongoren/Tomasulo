package units;

public class FPAddSub extends Unit {

	public FPAddSub(int delay) {
		super(delay);
	}
	
	public float execute(float input1, float input2) {
		return input1 + input2;
	}

}
