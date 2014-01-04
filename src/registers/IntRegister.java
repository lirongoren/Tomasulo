package registers;

public class IntRegister extends Register {
	private int value;

	public IntRegister(int value) {
		super();
		this.value = 0;
		this.name = "R" + value;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		this.status = Status.VALUE;
		this.tag = "";
	}

}
