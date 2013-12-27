package uoc.ei.practica;

import java.util.Comparator;

import uoc.ei.tads.Contenidor;
import uoc.ei.tads.CuaAmbPrioritat;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.LlistaEncadenada;
import uoc.ei.tads.Posicio;

/**
 * mètode que representa una estació del sistema
 *
 */
public class Station {

	/**
	 * comparador de les bicicletes d'una estació basat en el temps d'us.
	 */
	private static final Comparator BICYCLE_COMPARATOR = new Comparator<Bicycle>(){

		@Override
		public int compare(Bicycle bicycle1, Bicycle bicycle2) {
			return (int)(bicycle1.time()-bicycle2.time());
		}
		
	};
	
	/**
	 * identificador de l'estació
	 */
	private int stationId;
	
	/**
	 * coordenada GPS
	 */
	private long latitude;

	/**
	 * coordenada GPS
	 */
	private long longitude;
	
	/**
	 * llista de les bicicletes d'una estació
	 */
	private LlistaEncadenada<Bicycle> availableBicycles;
	
	/**
	 * llista de les bicicletes d'una estació avariades
	 */
	private LlistaEncadenada<Bicycle> breakdownBicycles;
	
	/**
	 * nombre de parking
	 */
	private int nParkings;
	
	/**
	 * activitat de l'estació
	 */
	private int activity;
	
	/**
	 * incidencias de l'estació
	 */
	private int problems;


	public Station(int stationId, long latitude, long longitude, int nParkings) {
		this.stationId=stationId;
		this.set(latitude, longitude, nParkings);
		this.availableBicycles=new LlistaEncadenada<Bicycle>();
		this.breakdownBicycles=new LlistaEncadenada<Bicycle>();
	}

	public void set(long latitude, long longitude, int nParkings) {
		this.latitude = latitude;
		this.longitude= longitude;
		this.nParkings=nParkings; 
	}

	/**
	 * mètode que afegeix una bicicleta a una estació
	 * @param bicycle
	 * @throws EIException
	 */
	public  Posicio<Bicycle> addBicycle(Bicycle bicycle) throws EIException {
		if(this.getFreeParkings()<=0) throw new EIException(Messages.MAX_NUMBER_OF_BICYCLES);
		
		bicycle.setCurrentStation(this);
		return this.availableBicycles.afegirAlFinal(bicycle);
	}

	/** 
	 * mètode que proporciona les bicicletes de l'estació
	 * @return
	 */
	public Contenidor<Bicycle> availableBicycles() {
		return this.availableBicycles;
	}
	
	/** 
	 * mètode que proporciona les bicicletes de l'estació
	 * @return
	 */
	public Contenidor<Bicycle> breakdownBicycles() {
		return this.breakdownBicycles();
	}
	
	
	/**
	 * mètode que proporciona una bicicleta disponible. Aquesta bicicleta serà 
	 * la bicicleta que s'hagi "usat" menys 
	 * @return
	 */
	public Bicycle getBicycle() {
		Bicycle auxBicycle=null;
		if (!this.availableBicycles.estaBuit()) {
			
			//get less used bike
		    Iterador<Bicycle> bicycleIt = this.availableBicycles.elements();
			auxBicycle = bicycleIt.seguent();
			Bicycle currentBicycle=null;
			
			while(bicycleIt.hiHaSeguent()){ 
				currentBicycle = bicycleIt.seguent();
				
				if(currentBicycle.time() > auxBicycle.time()){
					auxBicycle=currentBicycle;
				}
			
			}
		    //Delete bicycle from station
			this.availableBicycles.esborrar(auxBicycle.getParkingTadPosition());
		}
		return auxBicycle;
	}

	/**
	 * mètode que proporciona una representació en forma d'un string d'una estació
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer("id: "+this.stationId).append(Messages.LS);
		sb.append("latitude: ").append(this.latitude).append(Messages.LS);
		sb.append("longitude: ").append(this.longitude).append(Messages.LS);
		sb.append("nParkings: ").append(this.nParkings).append(Messages.LS);
		sb.append("bicycles: ").append(this.availableBicycles.nombreElems())
		.append(Messages.LS);
		
		return sb.toString();
		
	}

	/**
	 * mètode que proporciona l'identificador de l'estació
	 * @return identificador de l'estació
	 */
	public int getIdentifier() {
		return this.stationId;
	}
	
	/**
	 * mètode que incrementa l'activitat d'una estació
	 */
	public void incActivity() {
		this.activity++;
	}

	/**
	 * mètode que proporciona l'activitat d'una estació
	 * @return activitat d'una estació
	 */
	public int activity() {
		return this.activity;
	}
	
	public void incProblems() {
		this.problems++;
	}

	/**
	 * mètode que proporciona l'activitat d'una estació
	 * @return activitat d'una estació
	 */
	public int problems() {
		return this.problems;
	}

	/**
	 * meodo que proporciona la lista de bicicletas disponibles.
	 * @return this.availableBicycles
	 */
	public LlistaEncadenada<Bicycle> getAvailableBicycles() {
		return this.availableBicycles;
	}
	
	/**
	 * meodo que proporciona la lista de bicicletas averiadas.
	 * @return this.breakdownBicycles
	 */
	public LlistaEncadenada<Bicycle> getBreakdownBicycles() {
		return this.breakdownBicycles;
	}
	
	/**
	 * meodo que proporciona número de plazas libres.
	 * @return the number of free parkings
	 * 
	 */
	public int getFreeParkings(){
		int freeParking = this.nParkings - this.availableBicycles.nombreElems()
				- this.breakdownBicycles.nombreElems();
						
		return freeParking;
				
	}
	
	public long calculateDistance (long latitude, long longitude){
		long totalDistance =(long) Math.sqrt(Math.pow(latitude - this.latitude, 2)
				+Math.pow(longitude - this.longitude, 2));
		
		return totalDistance;
	}
}