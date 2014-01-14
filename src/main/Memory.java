package main;

public class Memory {
	 
    private int[] mem;
    private static int maxWords = 1024;
	private int insertPtr = 0;
    
	/**
	 * Creates an array of 1024 integers.
	 */
    public Memory(){	
    	mem =  new int[maxWords];
    }
    
    /**
     * @return the max words in the memory.
     */
    public static int getMaxWords() {
		return maxWords;
	}
    
    /**
     * Inserts data into the memory.
     * @param data the data that will be inserted into the memory.
     */
    public void insert(int data){
    	mem[insertPtr++] = data; 
    }
        
    /**
     * Stores the data into the pointer given in the memory.
     * @param pointer the location in which the data is stored.
     * @param data the data that will be stored.
     */
    public void store(int pointer, float data){  	 
        mem[pointer] = Float.floatToIntBits(data);
    } 
    
    /**
     * Loads the data from the given pointer in the memory.
     * @param pointer the location from which the data will be load.
     * @return the data from the memory.
     */
    public float load(int pointer){
    	return Float.intBitsToFloat(mem[pointer]); 
    }
    
    /**
     * Converts data from a given pointer into a hex string and loads it.
     * @param pointer the pointer that we will load from.
     * @return the data from the memory as a hex.
     */
    public String loadAsHexString(int pointer){
    	String result = Integer.toHexString(mem[pointer]);
		while (result.length() < 8) {
			result = "0" + result;
		}
		return result;
    }
    
    /**
     * Converts data from a given pointer into a binary string and loads it.
     * @param pointer the pointer that we will load from.
     * @return the data from the memory as a binary.
     */
	public String loadAsBinaryString(int pointer) {
		String result = Integer.toBinaryString(mem[pointer]);
		while (result.length()<32){
			result = "0" + result;
		}
		return result;
	}	
}
