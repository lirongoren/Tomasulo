package reservationStations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationStations {

	private Map<String, ReservationStation> reservationStationsMap;
	
	private List<MulOrAddReservationStation> mulReservationStations;
	private List<MulOrAddReservationStation> addReservationStations;
	private List<AluReservationStation> aluReservationStations;
	
	
	public ReservationStations(int numMulRS, int numAddRS, int numAluRS){	
		int i;
		reservationStationsMap = new HashMap<String, ReservationStation>();
		
		mulReservationStations = new ArrayList<MulOrAddReservationStation>();
		for (i=0 ; i<numMulRS ; i++){
			MulOrAddReservationStation res = new MulOrAddReservationStation(i, "MUL");
			mulReservationStations.add(res);
			reservationStationsMap.put(res.getNameOfStation(), res);
		}
		addReservationStations = new ArrayList<MulOrAddReservationStation>();
		for (i=0 ; i<numAddRS ; i++){
			MulOrAddReservationStation res = new MulOrAddReservationStation(i, "ADD");
			addReservationStations.add(res);
			reservationStationsMap.put(res.getNameOfStation(), res);
		}
		aluReservationStations = new ArrayList<AluReservationStation>();
		for (i=0 ; i<numAluRS ; i++){
			AluReservationStation res = new AluReservationStation(i, "ALU");
			aluReservationStations.add(res);
			reservationStationsMap.put(res.getNameOfStation(), res);
		}
	}
	
	public ReservationStation getReservationStation(String name) {
		return reservationStationsMap.get(name);
	}
	
	public boolean isThereFreeAluRS(){
		Collections.sort(aluReservationStations);
		if (aluReservationStations.get(0).isBusy()){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeMulRS(){
		Collections.sort(mulReservationStations);
		if (mulReservationStations.get(0).isBusy()){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeAddSubRS(){
		Collections.sort(addReservationStations);
		if (addReservationStations.get(0).isBusy()){
			return false;
		}
		return true;
	}
	
	public MulOrAddReservationStation getFreeAddReservationStation() {
		for (MulOrAddReservationStation reservationStation: addReservationStations) {
			if (!reservationStation.isBusy()) {
				return reservationStation;
			}
		}
		return null;
	}
	
	public MulOrAddReservationStation getFreeMulReservationStation() {
		for (MulOrAddReservationStation reservationStation: mulReservationStations) {
			if (!reservationStation.isBusy()) {
				return reservationStation;
			}
		}
		return null;
	}
	
	public AluReservationStation getFreeAluReservationStation() {
		for (AluReservationStation reservationStation: aluReservationStations) {
			if (!reservationStation.isBusy()) {
				return reservationStation;
			}
		}
		return null;
	}

	public void updateTags(String station, Object object) {
		for (AluReservationStation RS : aluReservationStations) {
			if (RS.getFirstTag().equals(station)) {
				RS.setValue1((int) object);
			}
			if (RS.getSecondTag().equals(station)) {
				RS.setValue2((int) object);
			}
		}
		for (MulOrAddReservationStation RS : mulReservationStations) {
			if (RS.getFirstTag().equals(station)) {
				RS.setValue1((float) object);
			}
			if (RS.getSecondTag().equals(station)) {
				RS.setValue2((float) object);
			}
		}
		for (MulOrAddReservationStation RS : addReservationStations) {
			if (RS.getFirstTag().equals(station)) {
				RS.setValue1((float) object);
			}
			if (RS.getSecondTag().equals(station)) {
				RS.setValue2((float) object);
			}
		}
	}
}
