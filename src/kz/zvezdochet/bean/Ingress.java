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
	 * Событие
	 */
	private Event event;
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

	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
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

	public Ingress(Event event, Planet planet, SkyPoint skyPoint, Model object, IngressType type) {
		super();
		this.event = event;
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
