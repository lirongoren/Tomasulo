package units;

import cdb.CDB;

public class Unit {
	int delay;
	CDB cdb;
	
	Unit(int delay, CDB cdb) {
		this.delay = delay;
		this.cdb = new CDB();
	}
	
	public void execute() {
		
	}
	
	public void writeToCDB() {
		
	}
}
