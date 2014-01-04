package units;

public class Unit {
	int delay;
	int numOfInstructionsWaiting;
	
	Unit(int delay) {
		this.delay = delay;
		this.numOfInstructionsWaiting = 0;
	}
	
	public int getDelay() {
		return delay;
	}

	public int getNumOfInstructionsWaiting() {
		return numOfInstructionsWaiting;
	}

	public void setNumOfInstructionsWaiting(int numOfInstructionsWaiting) {
		this.numOfInstructionsWaiting = numOfInstructionsWaiting;
	}
	
	public void increaseNumOfInstructionsWaiting() {
		this.numOfInstructionsWaiting++;
	}
	
	public void decreaseNumOfInstructionsWaiting() {
		this.numOfInstructionsWaiting--;
	}
	
}
