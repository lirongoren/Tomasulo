package buffers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Buffers {
	
	private List<StoreBuffer> storeBuffers;
	private List<LoadBuffer> loadBuffers;
	
	public Buffers(int numStoreBuffers, int numLoadBuffers){	
		int i;
		storeBuffers = new ArrayList<StoreBuffer>();
		for (i=0 ; i<numStoreBuffers ; i++){
			storeBuffers.add(new StoreBuffer(i, "STORE"));
		}
		
		loadBuffers = new ArrayList<LoadBuffer>();
		for (i=0 ; i<numLoadBuffers ; i++){
			loadBuffers.add(new LoadBuffer(i, "LOAD"));
		}
	}
	
	public boolean isThereFreeStoreBuffer(){
		Collections.sort(storeBuffers);
		if (storeBuffers.get(0).isBusy()==1){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeLoadBuffer(){
		Collections.sort(loadBuffers);
		if (loadBuffers.get(0).isBusy()==1){
			return false;
		}
		return true;
	}
}
