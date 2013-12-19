package registers;

public class Registers {
	IntRegister[] int_registers;
	FloatRegister[] float_registers;
	
	public Registers() {
		int_registers = new IntRegister[16];
		for (int i = 0; i < 16; i++) {
			int_registers[i] = new IntRegister(i);
		}
		float_registers = new FloatRegister[16];
		for (int i = 0; i < 16; i++) {
			float_registers[i] = new FloatRegister((float) i);
		}
	}
	
	public void setIntRegisterValue(int register_number, int value) {
		this.int_registers[register_number].setValue(value);
	}
	
	public void setFloatRegisterValue(int register_number, float value) {
		this.float_registers[register_number].setValue(value);
	}
	
	public void setIntRegisterTag(int register_number, String tag) {
		this.int_registers[register_number].setTag(tag);
	}
	
	public void setFloatRegisterTag(int register_number, String tag) {
		this.float_registers[register_number].setTag(tag);
	}
}
