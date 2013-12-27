package uoc.ei.practica;

import java.util.Date;


/**
 * clase que representa una incidencia en el sistema
 *
 */
/**
 * @author Usuario
 *
 */
public class Ticket extends IdentifiedObject implements Comparable<Ticket>{

	/**
	 * Bicicleta a la que se aasocia la incidencia
	 */
	private Bicycle bicycle;
	
	/**
	 * Descripcion de la incidencia
	 */
	private String description;
	
	/**
	 * Fecha de la última modificación
	 */
	private Date datetime;

	/**
	 * Operario asignado
	 */
	private Worker worker;
		
	/** 
	 * Rango de estados posibles
	 */
	public enum Status {
	    NOT_ASSIGNED, ASSIGNED, SOLVED
	}
	
	/** 
	 * Estado actual de la incidencia
	 */
	private Status status;
	
	
	/**
	 * @return this.status
	 */
	public Status getStatus() {
		return status;
	}

	/** 
	 * constructor
	 */
	public Ticket(int ticketId, Bicycle bicycle, String description, Date datetime) {
		super(Integer.toString(ticketId));
		this.bicycle = bicycle;
		this.description = description;
		this.datetime = datetime;
		this.worker = null;
		this.status = Status.NOT_ASSIGNED;
	}

	/**
	 * mètode que defineix l'ordre natural d'una incidencia.
	 */
	@Override
	public int compareTo(Ticket arg0) {
		return this.identifier.compareTo(arg0.identifier);
	}
	
	/**
	 * override de toString para ajustar el output
	 */
	public String toString (){
		StringBuffer sb=new StringBuffer();
		sb.append("id: ").append(this.identifier).append(Messages.LS)
		.append("description: ").append(this.description).append(Messages.LS)
		.append("bicycle: ").append(this.bicycle.getIdentifier()).append(Messages.LS)
		.append("status: ").append(this.status).append(Messages.LS)
		.append("dateTime: ").append(DateUtils.format(this.datetime)).append(Messages.LS);
		if(this.status==Status.ASSIGNED){
			sb.append("worker: ").append(this.worker.getName()).append(Messages.LS);
		}
		 		
		
		
		return sb.toString();
	}
	
	
	/**
	 * Metodo que asigna un operario a esta incidencia
	 */
	public void assingTicket (Worker worker, Date datetime){
		this.worker = worker;
		this.status = Status.ASSIGNED;
		this.datetime = datetime;
	}
	
	
	/**
	 * Metodo que marca la incidencia como resuelta
	 */
	public void solveTicket(String description, Date datetime){
		this.status = Status.SOLVED;
		this.datetime = datetime;
		this.description = description;
	}
	

	/**
	 * Metodo que retorna el operario asignado
	 */
	public Worker getWorker(){
		return this.worker;
	}

	public Bicycle getBicycle() {
		return bicycle;
	}
	
}
