package buffers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buffers {
	
	private Map<String, LoadStoreBuffer> buffers;
	private List<StoreBuffer> storeBuffers;
	private List<LoadBuffer> loadBuffers;

	public Buffers(int numStoreBuffers, int numLoadBuffers){	
		int i;
		buffers = new HashMap<String, LoadStoreBuffer>();
		
		storeBuffers = new ArrayList<StoreBuffer>();
		for (i=0 ; i<numStoreBuffers ; i++){
			StoreBuffer storeBuffer = new StoreBuffer(i, "STORE");
			storeBuffers.add(storeBuffer);
			buffers.put(storeBuffer.getNameOfStation(), storeBuffer);
		}
		
		loadBuffers = new ArrayList<LoadBuffer>();
		for (i=0 ; i<numLoadBuffers ; i++){
			LoadBuffer loadBuffer = new LoadBuffer(i, "LOAD");
			loadBuffers.add(loadBuffer);
			buffers.put(loadBuffer.getNameOfStation(), loadBuffer);
		}
	}
	
	public Map<String, LoadStoreBuffer> getBuffers() {
		return buffers;
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
		return (LoadBuffer) buffers.get(name);
	}
	
	public StoreBuffer getStoreBuffer(String name) {
		return (StoreBuffer) buffers.get(name);
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
				buffer.setValue1((int) object);
			}
		}
		for (StoreBuffer buffer : storeBuffers) {
			if (buffer.getFirstTag().equals(station)) {
				buffer.setValue1((int) object);
			}
			if (buffer.getSecondTag().equals(station)) {
				buffer.setValue2((float) object);
			}
		}
	}

	public boolean isThereAddressCollision() {
		// TODO Auto-generated method stub
		return false;
	}

	public List<StoreBuffer> getStoreBuffers() {
		return storeBuffers;
	}

	public List<LoadBuffer> getLoadBuffers() {
		return loadBuffers;
	}
	
	public void freeBuffer(String station) {
		LoadStoreBuffer buffer = buffers.get(station);
		if (buffer.isReady()) {
			buffer.free();
		}
	}
	
	
}
