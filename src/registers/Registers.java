package registers;

import java.io.BufferedWriter;
import java.io.IOException;

import registers.Register.Status;

public class Registers {
	private IntRegister[] int_registers;
	private FloatRegister[] float_registers;
	
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
	
	public String getIntRegisterTag(int register_number) {
		return this.int_registers[register_number].getTag();
	}
	
	public String getFloatRegisterTag(int register_number) {
		return this.float_registers[register_number].getTag();
	}
	
	public int getIntRegisterValue(int register_number) {
		return this.int_registers[register_number].getValue();
	}
	
	public float getFloatRegisterValue(int register_number) {
		return this.float_registers[register_number].getValue();
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
	
	public Status getIntRegisterStatus(int register_number) {
		return int_registers[register_number].getStatus();
	}
	
	public Status getFloatRegisterStatus(int register_number) {
		return float_registers[register_number].getStatus();
	}

	public void printFloatRegisters(BufferedWriter output) throws IOException {
		for (FloatRegister reg : float_registers) {
			output.write(reg.toString());
			output.newLine();
		}
	}
}
