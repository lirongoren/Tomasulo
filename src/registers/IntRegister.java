package registers;

public class IntRegister {
	private String name;
	private int value;
	
	IntRegister(String name) {
		this.name = name;
		this.setValue(Integer.parseInt(name.substring(1)));
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
