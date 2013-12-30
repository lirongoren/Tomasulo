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
	
	public LoadBuffer getFreeLoadBuffer() {
		for (LoadBuffer buffer: loadBuffers) {
			if (!buffer.isBusy()) {
				return buffer;
			}
		}
		return null;
	}
	
	public StoreBuffer getFreeStoreBuffer() {
		for (StoreBuffer buffer: storeBuffers) {
			if (!buffer.isBusy()) {
				return buffer;
			}
		}
		return null;
	}
	
	public LoadBuffer getLoadBuffer(String name) {
		for (LoadBuffer loadBuffer : loadBuffers) {
			if (loadBuffer.getNameOfStation().equals(name)) {
				return loadBuffer;
			}
		}
		return null;
	}
	
	public StoreBuffer getStoreBuffer(String name) {
		for (StoreBuffer storeBuffer : storeBuffers) {
			if (storeBuffer.getNameOfStation().equals(name)) {
				return storeBuffer;
			}
		}
		return null;
	}
		
	public boolean isThereFreeStoreBuffer(){
		Collections.sort(storeBuffers);
		if (storeBuffers.get(0).isBusy()){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeLoadBuffer(){
		Collections.sort(loadBuffers);
		if (loadBuffers.get(0).isBusy()){
			return false;
		}
		return true;
	}
	
	public void updateTags(String station, Object object) {
		for (LoadBuffer buffer : loadBuffers) {
			if (buffer.getFirstTag().equals(station)) {
//				buffer.setValue1((int) object); // TODO - implement
			}
		}
		for (StoreBuffer buffer : storeBuffers) {
			if (buffer.getFirstTag().equals(station)) {
//				buffer.setValue1((int) object); // TODO - implement
			}
		}
	}
}
