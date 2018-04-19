package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.IngressService;

/**
 * Ингрессия планеты события
 * @author Nataly Didenko
 */
public class Ingress extends Model {
	private static final long serialVersionUID = 5670761424933047181L;
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
	private SkyPoint skyPoint;
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
	public SkyPoint getSkyPoint() {
		return skyPoint;
	}
	public void setSkyPoint(SkyPoint skyPoint) {
		this.skyPoint = skyPoint;
	}
	public IngressType getType() {
		return type;
	}
	public void setType(IngressType type) {
		this.type = type;
	}

	public Ingress(long eventid, Planet planet, SkyPoint skyPoint, Model object, IngressType type) {
		super();
		this.eventid = eventid;
		this.planet = planet;
		this.skyPoint = skyPoint;
		this.object = object;
		this.type = type;
	}

	public Ingress() {}

	@Override
	public ModelService getService() {
		return new IngressService();
	}

	@Override
	public void init(boolean mode) {}	

	public Model getObject() {
		return object;
	}
	public void setObject(Model object) {
		this.object = object;
	}

	/**
	 * Связанный объект (аспект, знак Зодиака и т.п.)
	 */
	private Model object;
}
