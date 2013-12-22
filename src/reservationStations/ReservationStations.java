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
	
	public boolean isThereFreeAluRS(){
		Collections.sort(aluReservationStations);
		if (aluReservationStations.get(0).isBusy()==1){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeMulRS(){
		Collections.sort(mulReservationStations);
		if (mulReservationStations.get(0).isBusy()==1){
			return false;
		}
		return true;
	}
	
	public boolean isThereFreeAddRS(){
		Collections.sort(addReservationStations);
		if (addReservationStations.get(0).isBusy()==1){
			return false;
		}
		return true;
	}
}
