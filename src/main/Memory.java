package main;

public class Memory {
	 
    private int[] mem;
    private int maxWords = 1024;
   
	private int insertPtr = 0;
    
    public Memory(){	
    	mem =  new int[maxWords];
    }
    
    /**
     * 
     * @return
     */
    public int getMaxWords() {
		return maxWords;
	}
    
    /**
     * 
     * @param data
     */
    public void insert(int data){
    	mem[insertPtr++] = data; 
    }
        
    /**
     * 
     * @param pointer
     * @param data
     */
    public void store(int pointer, int data){  	 
        mem[pointer] = data;
    } 
    
    /**
     * 
     * @param pointer
     * @return
     */
    public int load(int pointer){
    	return mem[pointer]; 
    }
    
    public String loadAsHexString(int pointer){
    	String result = Integer.toHexString(mem[pointer]);
		while (result.length() < 8) {
			result = "0" + result;
		}
		return result;
    }
    
    /**
     * 
     * @param pointer
     * @return
     */
	public String loadAsBinaryString(int pointer) {
		String result = Integer.toBinaryString(mem[pointer]);
		while (result.length()<32){
			result = "0" + result;
		}
		return result;
	}	
}
