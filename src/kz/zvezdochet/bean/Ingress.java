package kz.zvezdochet.bean;

/**
 * Ингрессия планеты события
 * @author Nataly Didenko
 */
public class Ingress {

	/**
	 * Идентификатор события
	 */
	private long eventid;
	/**
	 * Планета
	 */
	private Planet planet;
	/**
	 * связанный объект
	 */
	private Object object;
	/**
	 * Тип
	 */
	private IngressType type;

	public long getEventid() {
		return eventid;
	}
	public void setEventid(long eventid) {
		this.eventid = eventid;
	}
	public Planet getPlanet() {
		return planet;
	}
	public void setPlanet(Planet planet) {
		this.planet = planet;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public IngressType getType() {
		return type;
	}
	public void setType(IngressType type) {
		this.type = type;
	}

	public Ingress(long eventid, Planet planet, Object object, IngressType type) {
		super();
		this.eventid = eventid;
		this.planet = planet;
		this.object = object;
		this.type = type;
	}	
}
