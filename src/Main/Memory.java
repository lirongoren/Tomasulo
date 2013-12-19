package Main;

public class Memory {
	 
    private Integer[] mem = new Integer[4096];
    int insertPtr = 0;
    
    public Memory(){	
    	
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
    
    /**
     * 
     * @param instNum
     * @return
     */
	public String getInst(int instNum) {
		String result = "";
		int firstPtr = instNum * 4;
		
		for (int k=0 ; k<4; k++){
			String intString = Integer.toString(load(firstPtr++), 2);
			while (intString.length()<8){
				intString = "0" + intString;
			}	
			result = result + intString;
		}
		return result;
		
	}
	
}
