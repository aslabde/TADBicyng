package uoc.ei.practica;

import java.util.Comparator;
import java.util.Date;

import uoc.ei.tads.Contenidor;
import uoc.ei.tads.Llista;
import uoc.ei.tads.LlistaEncadenada;
import uoc.ei.tads.Posicio;

/**
 * Mètode que representa una bicicleta en el sistema
 *
 */
/**
 * @author Usuario
 *
 */
public class Bicycle extends IdentifiedObject implements Comparable<Bicycle> {

	private String model;
	private long totalTime;
	private Llista<Service> services;
	private Service currentService;
	
	public enum BicycleStatus {
		ON_SERVICE, ON_TROUBLE
	}
	
	private Posicio<Bicycle> parkingTadPosition;
	private Station currentStation;
	private BicycleStatus status;
	private LlistaEncadenada<Ticket> bicycleTicketsList;
	
	/**
	 * comparador que defineix l'ordre global entre bicicletes
	 */
	public static Comparator<String>  COMP = new Comparator<String>() {
		public int compare(String arg0, String arg1) {
			return arg0.compareTo(arg1);
		}		
	};
	

	public Bicycle(String bicycleId, String model) {
		this(bicycleId);
		this.model=model;
		this.services = new LlistaEncadenada<Service>();
		this.currentService=null;
		this.status = BicycleStatus.ON_SERVICE;
		this.bicycleTicketsList = new LlistaEncadenada<Ticket>();
		this.currentStation=null;
		this.parkingTadPosition=null;
	}

	public Bicycle(String bicycleId) {
		super(bicycleId);
	}

	public long time() {
		return this.totalTime;
	}
	

	/**
	 * mètode que proporciona els serveis d'una bicicleta
	 * @return
	 */
	public Contenidor<Service> services() {
		return this.services;
	}

	/**
	 * mètode que indica l'inici d'un Servei
	 * @param user usuari que agafa la bicicleta
	 * @param station estació 
	 * @param dateTime data en la que s'inicia un servei
	 */
	public void startService(User user, Station station, Date dateTime) {
		Service service= new Service(user, station, dateTime);
		this.services.afegirAlFinal(service);
		this.currentService=service;
		user.addCurrentService(service);
		this.currentStation=null;
	}

	/**
	 * mètode que indica el final d'un Servei
	 * @param toStationId estació destí
	 * @param dateTime data en la que finalitza el servei
	 */
	public void finishService(Station toStationId, Date dateTime) {
		long time = this.currentService.finish(toStationId, dateTime);
		User user = this.currentService.getUser();
		user.finishCurrentService();
		this.currentService=null;
		this.totalTime+=time;
		this.currentStation=toStationId;
	}
	
	/**
	 * mètode que proporciona una representació en forma de String d'una bicicleta
	 */
	public String toString() {
		StringBuffer sb=new StringBuffer(super.toString());
		sb.append("model: ").append(this.model).append(Messages.LS);
		sb.append("time of use: ").append(DateUtils.diffHours(this.totalTime)).append(" hours");
		if (this.totalTime!=0) sb.append(" (timeMilis: ").append(this.totalTime+") ").append(Messages.LS);
		else sb.append(Messages.LS);
		
		if (this.currentService!=null) sb.append(Messages.RUNNING).append(Messages.LS);
		
		return sb.toString();
		
	}

	/**
	 * mètode que defineix l'ordre natural d'una bicicleta.
	 */
	@Override
	public int compareTo(Bicycle arg0) {
		return this.identifier.compareTo(arg0.identifier);
	}
	
	/**
	 * Metodo que marca la bicicleta como operativa y la cambia de lista
	 */
	public void changeStatusToOnService(){
		this.status = BicycleStatus.ON_SERVICE;
		this.currentStation.getBreakdownBicycles().esborrar(this.parkingTadPosition);
		this.parkingTadPosition = this.currentStation.getAvailableBicycles().afegirAlFinal(this);
	}
	
	/**
	 * Metodo que marca la bicicleta como averiada y la cambia de lista
	 */
	public void changeStatusToOnTrouble(){
		this.status = BicycleStatus.ON_TROUBLE;
		this.currentStation.getAvailableBicycles().esborrar(this.parkingTadPosition);
		this.parkingTadPosition = this.currentStation.getBreakdownBicycles().afegirAlFinal(this);
	}

	
	
	/**
	 * @return lista de tickets asociados a la bibicleta
	 */
	public LlistaEncadenada<Ticket> getBicycleTicketsList() {
		return this.bicycleTicketsList;
	}

	/**
	 * @return this.currenStation
	 */
	public Station getCurrentStation(){
		return this.currentStation;
	}
	
	/**
	 * @return this.currenStation
	 */
	public void setCurrentStation(Station station){
		 this.currentStation = station;
	}

	/**
	 * @return this.parkingTadPosition
	 */
	public Posicio<Bicycle> getParkingTadPosition() {
		return this.parkingTadPosition;
	}

	/**
	 * @param parkingTadPosition
	 */
	public void setParkingTadPosition(Posicio<Bicycle> parkingTadPosition) {
		this.parkingTadPosition = parkingTadPosition;
	}

	/**
	 * @return status
	 */
	public BicycleStatus getStatus() {
		return status;
	}
}
