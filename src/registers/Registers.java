package registers;

import java.util.HashMap;

public class Registers {
	HashMap<String, IntRegister> int_registers = new HashMap<>();
	HashMap<String, FloatRegister> float_registers = new HashMap<>();
	
	public Registers() {
		String register_name;
		for (int i = 0; i < 16; i++) {
			register_name = "R" + i;
			int_registers.put(register_name, new IntRegister(register_name));
		}
		for (int i = 0; i < 16; i++) {
			register_name = "F" + i;
			float_registers.put(register_name, new FloatRegister(register_name));
		}
	}
	
	public void setIntRegisterValue(String name, int value) {
		this.int_registers.get(name).setValue(value);
	}
	
	public void setFloatRegisterValue(String name, float value) {
		this.float_registers.get(name).setValue(value);
	}
}
