package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.PlanetSignPositionService;

/**
 * Позиция планеты в астрологическом доме
 * @author Natalie Didenko
 */
public class PlanetHousePosition extends Dictionary {
	private static final long serialVersionUID = 2397422588602704899L;

	/**
	 * Планета
	 */
    private Planet planet;
    
    public Planet getPlanet() {
		return planet;
	}
	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	/**
	 * Тип
	 */
	private PositionType type;

	public PositionType getType() {
		return type;
	}
	public void setType(PositionType type) {
		this.type = type;
	}

	/**
	 * Астрологический дом
	 */
	private House house;

	public House getHouse() {
		return house;
	}
	public void setHouse(House house) {
		this.house = house;
	}
	@Override
	public ModelService getService() {
		return new PlanetSignPositionService();
	}
}
