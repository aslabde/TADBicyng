package uoc.ei.practica;



/**
 * clase que representa un operari en el sistema
 *
 */
public class Worker extends IdentifiedObject implements Comparable<Worker> {
	

	/** 
	 * nom de l'operari
	 */
	private String name;
	
	
	/**
	 * @return this.name
	 */
	public String getName() {
		return name;
	}

	/** 
	 * total de incidencias asignadas
	 */
	private int numTickets;
	
	
	/** 
	 * constructor
	 */
	public Worker(String workerId, String name) {
		super(workerId);
		this.name=name;
		this.numTickets=0;
	}
	

	/**
	 * mètode que incrementa el nombre d' tickets de l'operari
	 */
	public void assignedTiket() {
		this.numTickets++;
	}
	
	/**
	 * override de toString para ajustar el output
	 */
	public String toString (){
		
		StringBuffer sb=new StringBuffer();
		sb.append("dni: ").append(this.identifier).append(Messages.LS)
		.append("name: ").append(this.name).append(Messages.LS)
		.append("numTickets: ").append(this.numTickets).append(Messages.LS);
		
		return sb.toString();
	}
	
	
	/**
	 * mètode que defineix l'ordre natural d'un operari.
	 */
	@Override
	public int compareTo(Worker arg0) {
		int a = this.getNumTickets();
		int b = arg0.getNumTickets();
		
		return (a > b ? +1 : a < b ? -1 : 0);
	}

	/**
	 * @return this.numTickets
	 */
	public int getNumTickets() {
		return numTickets;
	}

	/**
	 * Metodo que incrementa las incidencias asociadas a un operario
	 */
	public void incTickets(){
		this.numTickets ++;
	}
	
	
}
