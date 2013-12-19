package registers;

public class FloatRegister {
	private String name;
	private float value;

	FloatRegister(String name) {
		this.name = name;
		this.setValue(Float.parseFloat(name.substring(1)));
	}

	public String getName() {
		return name;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

}