package reservationStations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservationStations {

//	Map<String, ReservastionStation> reservationStationsMap;
	
	private List<MulOrAddReservationStation> mulReservationStations;
	private List<MulOrAddReservationStation> addReservationStations;
	private List<AluReservationStation> aluReservationStations;
	
	
	public ReservationStations(int numMulRS, int numAddRS, int numAluRS){	
		int i;
		mulReservationStations = new ArrayList<MulOrAddReservationStation>();
		for (i=0 ; i<numMulRS ; i++){
			mulReservationStations.add(new MulOrAddReservationStation(i, "MUL"));
		}
		addReservationStations = new ArrayList<MulOrAddReservationStation>();
		for (i=0 ; i<numAddRS ; i++){
			addReservationStations.add(new MulOrAddReservationStation(i, "ADD"));
		}
		aluReservationStations = new ArrayList<AluReservationStation>();
		for (i=0 ; i<numAluRS ; i++){
			aluReservationStations.add(new AluReservationStation(i, "ALU"));
		}
	}
	
	public ReservationStation getReservationStation(String name) {
		for (MulOrAddReservationStation RS : mulReservationStations) {
			if (RS.getNameOfStation().equals(name)) {
				return RS;
			}
		}
		for (MulOrAddReservationStation RS : addReservationStations) {
			if (RS.getNameOfStation().equals(name)) {
				return RS;
			}
		}
		for (AluReservationStation RS : aluReservationStations) {
			if (RS.getNameOfStation().equals(name)) {
				return RS;
			}
		}
		return null;
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
				RS.setValue1((int) object);
			}
			if (RS.getSecondTag().equals(station)) {
				RS.setValue2((int) object);
			}
		}
		for (MulOrAddReservationStation RS : addReservationStations) {
			if (RS.getFirstTag().equals(station)) {
				RS.setValue1((int) object);
			}
			if (RS.getSecondTag().equals(station)) {
				RS.setValue2((int) object);
			}
		}
	}
}
