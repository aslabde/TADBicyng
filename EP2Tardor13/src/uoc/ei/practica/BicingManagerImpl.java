package uoc.ei.practica;

import java.util.Date;

import javax.annotation.Resource.AuthenticationType;

import com.sun.xml.internal.bind.marshaller.MinimumEscapeHandler;

import uoc.ei.practica.Bicycle.BicycleStatus;
import uoc.ei.practica.Ticket.Status;
import uoc.ei.tads.Contenidor;
import uoc.ei.tads.CuaAmbPrioritat;
import uoc.ei.tads.DiccionariAVLImpl;
import uoc.ei.tads.Iterador;
import uoc.ei.tads.IteradorVectorImpl;
import uoc.ei.tads.LlistaEncadenada;
import uoc.ei.tads.TaulaDispersio;

public class BicingManagerImpl implements BicingManager {
	
	/**
	 * vector d'estacions del sistema
	 */
	private Station[] stations;
	private int len;
	
	/**
	 * vector ordenat global de bicicletes del sistema
	 */
	private TaulaDispersio<String, Bicycle> bicycles;
	
	/**
	 * AVL d'usuaris del sistema
	 */
	private DiccionariAVLImpl<String, User> users;
	
	/**
	 * cua d'operaris del sistema
	 */
	private CuaAmbPrioritat<Worker> workers;
	
	/** 
	 * apuntador a l'estació més activa
	 */
	private Station mostActiveStation;
	
	/**
	 * apuntador a l'usuari més actiu
	 */
	private User mostActiveUser;
	
	/**
	 * Contador total para aisgnar ID a los tickets
	 */
	private int globalNumTicket;
	
	/**
	 * AVL d'tickets del sistema
	 */
	private DiccionariAVLImpl<String, Ticket> tickets;
	
	/** 
	 * apuntador a l'estació més problematica
	 */
	private Station mostProblematicStation;

	public BicingManagerImpl() {
		this.stations= new Station[S];
		this.len=0;
		this.bicycles= new  TaulaDispersio<String, Bicycle>(B);
		this.users= new DiccionariAVLImpl<String, User>();
		this.workers = new CuaAmbPrioritat<Worker>();
		this.mostActiveStation=null;
		this.mostActiveUser=null;
		this.globalNumTicket = 0;
		this.tickets = new DiccionariAVLImpl<String, Ticket>();
		this.mostProblematicStation=null;
	}


	@Override
	public void addStation(int stationId, long latitude, long longitude,
			int nParkings) throws EIException {
		Station station = this.getStation(stationId);
		
		if (station!=null) station.set(latitude, longitude, nParkings);
		else {
			station = new Station(stationId, latitude, longitude, nParkings);
			this.stations[stationId-1]=station;
			if (len<stationId) len=stationId;
		}
		
		
	}


	public Station getStation(int stationId) {
		Station ret=null;
		if (stationId<=this.len) 
			ret = this.stations[stationId-1];
		return ret;
	}
	
	public Station getStation(int stationId, String message) throws EIException {
		Station station = this.getStation(stationId);
		if (station==null) throw new EIException(message);
		
		return station;
	}
	
	@Override
	public Iterador<Station> stations() throws EIException {
		if (this.len==0) throw new EIException(Messages.NO_STATIONS);
		Iterador<Station> it =  new IteradorVectorImpl(this.stations,this.len,0);

		return it;
	}


	@Override
	public void addBicycle(String bicycleId, String model, int stationId)
			throws EIException {
		
		Bicycle bicycle = this.bicycles.consultar(bicycleId); 
		if (bicycle!=null) throw new EIException(Messages.BICYCLE_ALREADY_EXISTS);
		
			
		Station station = this.getStation(stationId, Messages.STATION_NOT_FOUND);
		
		bicycle = new Bicycle(bicycleId, model);
		bicycle.setParkingTadPosition( station.addBicycle(bicycle));
		this.bicycles.afegir(bicycleId, bicycle);
		
	}


	@Override
	public Iterador<Bicycle> bicyclesByStation(int stationId)
			throws EIException {
		Station station = this.getStation(stationId, Messages.STATION_NOT_FOUND);
		Contenidor<Bicycle> bicycles = station.availableBicycles();
		if (bicycles.estaBuit()) throw new EIException(Messages.NO_BICYCLES);

		
		return bicycles.elements();
	}


	@Override
	public Iterador<Bicycle> bicycles() throws EIException {
		if (this.bicycles.estaBuit()) throw new EIException(Messages.NO_BICYCLES);

		return bicycles.elements();
	}


	@Override
	public void addUser(String userId, String name) {
		User user = this.users.consultar(userId);
		
		if (user!=null) user.set(name);
		else {
			user = new User(userId, name);
			this.users.afegir(userId, user);
		}
		
	}


	@Override
	public Iterador<User> users() throws EIException {
		if (this.users.estaBuit()) throw new EIException(Messages.NO_USERS);

		
		return users.elements();
	}


	@Override
	public Bicycle getBicycle(String userId, int fromStationId, Date dateTime)
			throws EIException {
		User user = this.users.consultar(userId);
		if (user == null) throw new EIException(Messages.USER_NOT_FOUND);
		Station station = this.getStation(fromStationId, Messages.STATION_NOT_FOUND);
		if (user.hasCurrentService()) throw new EIException(Messages.USER_IS_BUSY);
	
		Bicycle bicycle = station.getBicycle();
		if (bicycle == null) throw new EIException(Messages.NO_BICYCLES);
		
		
		bicycle.startService(user, station, dateTime);
		station.incActivity();
		user.incActicity();
		
		if (this.mostActiveStation==null || this.mostActiveStation.activity()<station.activity()) this.mostActiveStation=station;
		if (this.mostActiveUser==null || this.mostActiveUser.activity()<user.activity()) this.mostActiveUser=user;
		
		return bicycle;
	}


	@Override
	public void returnBicycle(String bicycleId, int toStationId, Date dateTime)
			throws EIException {
		
		Bicycle bicycle = this.bicycles.consultar(bicycleId);
		if(bicycle == null) throw new EIException(Messages.BICYCLE_NOT_FOUND);
		
		Station station = this.getStation(toStationId, Messages.STATION_NOT_FOUND);
		bicycle.setParkingTadPosition(station.addBicycle(bicycle)); 
		
		bicycle.finishService(station, dateTime);
		
	}


	@Override
	public Iterador<Service> servicesByBicycle(String bicycleId)
			throws EIException {
		Bicycle bicycle = this.bicycles.consultar(bicycleId);
		if(bicycle == null) throw new EIException(Messages.BICYCLE_NOT_FOUND);
		
		Contenidor<Service> services= bicycle.services();
		
		if (services.estaBuit()) throw new EIException(Messages.NO_SERVICES);
		
		return services.elements();
	}


	@Override
	public Station mostActiveStation() throws EIException {
		if (this.mostActiveStation==null) throw new EIException(Messages.NO_STATIONS);
		return this.mostActiveStation;
	}


	@Override
	public User mostActiveUser() throws EIException {
		if (this.mostActiveUser==null) throw new EIException(Messages.NO_USERS);
		return this.mostActiveUser;
	}


	@Override
	public void addWorker(String dni, String name){
		
		Worker worker = new Worker(dni, name);
		this.workers.encuar(worker);
		
	}


	@Override
	public Iterador<Worker> workers() throws EIException {
			if (this.workers.estaBuit()) throw new EIException(Messages.NO_WORKERS);

			return this.workers.elements();
	}


	@Override
	public int addTicket(String bicicleId, String description, Date dateTime)
			throws EIException {
		Bicycle bicycle = this.bicycles.consultar(bicicleId);
		if (bicycle==null) throw new EIException(Messages.BICYCLE_NOT_FOUND);
		
				
		//Move bicycle from available to breakdown list
		if(bicycle.getStatus()==BicycleStatus.ON_SERVICE){
			bicycle.changeStatusToOnTrouble();
					
		}
		
		Ticket ticket = new Ticket((this.globalNumTicket + 1), bicycle, description, dateTime);
		this.tickets.afegir(ticket.identifier, ticket);
		bicycle.getBicycleTicketsList().afegirAlPrincipi(ticket);
		
		Station station = bicycle.getCurrentStation();
		if(station==null) throw new EIException(Messages.NOT_IN_PARKING);
		
		station.incProblems();
		if (this.mostProblematicStation==null || this.mostProblematicStation.problems()<station.problems())
				this.mostProblematicStation=station;
		
		this.globalNumTicket ++;
		return this.globalNumTicket;	
	}


	@Override
	public Iterador<Ticket> tickets() throws EIException {
			if (this.tickets.estaBuit()) throw new EIException(Messages.NO_TICKETS);

		
		return this.tickets.elements();
	}


	@Override
	public Worker assignTicket(int ticketId, Date dateTime) throws EIException {
		
		Ticket ticket = this.tickets.consultar(Integer.toString(ticketId));
		if (ticket==null) throw new EIException(Messages.TICKET_NOT_FOUND);
		
		if (this.workers.estaBuit()) throw new EIException(Messages.NO_WORKERS);
		Worker worker = this.workers.desencuar();
		
		ticket.assingTicket(worker, dateTime);
		worker.incTickets();
		this.workers.encuar(worker);
		
		return worker;
	}


	@Override
	public void resolveTicket(int ticketId, String desciption, Date dateTime)
			throws EIException {
		Ticket ticket = this.tickets.consultar(Integer.toString(ticketId));
		if (ticket==null) throw new EIException(Messages.TICKET_NOT_FOUND);
		if (ticket.getStatus()!=Status.ASSIGNED) throw new EIException(Messages.NOT_ASSINGNED);
		
		ticket.solveTicket(desciption,dateTime);
		Bicycle bicycle = ticket.getBicycle();
		bicycle.changeStatusToOnService();
	
	}


	@Override
	public Iterador<Ticket> ticketsByWorker(String workerId) throws EIException {
		LlistaEncadenada< Ticket> ticketsWorker = new LlistaEncadenada<Ticket>();
		
		Iterador<Ticket> it = this.tickets();
		
		while (it.hiHaSeguent()){
		    Ticket ticket = (Ticket)it.seguent();  
			if(ticket.getWorker()!=null){	
		    	if(ticket.getWorker().identifier.equals(workerId) && ticket.getStatus()==Status.ASSIGNED){
			    	  ticketsWorker.afegirAlFinal(ticket);
			      }
			}	
		}
		
		if(ticketsWorker.estaBuit()) throw new EIException(Messages.NO_TICKETS);
		
		return ticketsWorker.elements();
	}


	@Override
	public Iterador<Ticket> ticketsByBicycle(String bicycleId)
			throws EIException {
		
		Bicycle bicycle = this.bicycles.consultar(bicycleId);
		if (bicycle==null) throw new EIException(Messages.BICYCLE_NOT_FOUND);
		
		if (bicycle.getBicycleTicketsList().estaBuit()) throw new EIException(Messages.NO_TICKETS);
		
		return bicycle.getBicycleTicketsList().elements();
		
	}


	@Override
	public Station mostProblematicStation() throws EIException {
		
		if (this.mostProblematicStation==null) throw new EIException(Messages.NO_TICKETS);
		
		return this.mostProblematicStation;
	}


	@Override
	public int getNBicycles(int stationId) throws EIException {
		Station station = this.getStation(stationId, Messages.STATION_NOT_FOUND);
		return station.availableBicycles().nombreElems();
	}


	@Override
	public int getNParkings(int stationId) throws EIException {
		Station station = this.getStation(stationId, Messages.STATION_NOT_FOUND);
		if (station.getFreeParkings() <= 0)  throw new EIException(Messages.NO_FREE_PARKINGS);
		
		return station.getFreeParkings();
	}


	@Override
	public Station getClosestBike(long latitude, long longitude)
			throws EIException {
		
				
		Iterador<Station> stationIt = this.stations();
		Station auxstation =stationIt.seguent();
		Station currentStation=null;
	
		while(stationIt.hiHaSeguent()){ 
			currentStation = stationIt.seguent();
			
			if(currentStation.calculateDistance(latitude, longitude) 
					< auxstation.calculateDistance(latitude, longitude) 
					&& this.getNBicycles(currentStation.getIdentifier()) > 0 ){
				
				auxstation=currentStation;
			}
	
		
		}
		if (this.getNBicycles(auxstation.getIdentifier())<= 0 ) throw new EIException(Messages.NO_BICYCLES);
		return auxstation;
	}


	@Override
	public Station getClosestParking(long latitude, long longitude)
			throws EIException {
		
		Iterador<Station> stationIt = this.stations();
		Station auxstation =stationIt.seguent();
		Station currentStation=null;
	
		while(stationIt.hiHaSeguent()){ 
			currentStation = stationIt.seguent();
			
			if(currentStation.calculateDistance(latitude, longitude) 
					< auxstation.calculateDistance(latitude, longitude) 
					&& this.getNParkings(currentStation.getIdentifier()) > 0 ){
				
				auxstation=currentStation;
			}
	
		
		}
		if (this.getNParkings(auxstation.getIdentifier())<= 0 ) throw new EIException(Messages.NO_FREE_PARKINGS);
		return auxstation;
	}
	

}
