package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.ModelService;
import kz.zvezdochet.service.PlanetSignPositionService;

/**
 * Позиция планеты в знаке
 * @author Natalie Didenko
 */
public class PlanetSignPosition extends Dictionary {
	private static final long serialVersionUID = -4031348639400070887L;

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
	 * Знак Зодиака
	 */
	private Sign sign;

	public Sign getSign() {
		return sign;
	}
	public void setSign(Sign sign) {
		this.sign = sign;
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
	@Override
	public ModelService getService() {
		return new PlanetSignPositionService();
	}
}
