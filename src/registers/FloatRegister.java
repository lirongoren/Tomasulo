package registers;

import registers.Register.Status;

public class FloatRegister extends Register {
	private float value;

	public FloatRegister(float value) {
		super();
		this.value = value;
		this.name = "F" + value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
		this.status = Status.VALUE;
	}

}