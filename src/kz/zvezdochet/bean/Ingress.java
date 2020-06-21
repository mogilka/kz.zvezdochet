package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Model;

/**
 * Ингрессия планеты
 * @author Natalie Didenko
 */
public class Ingress {
	/**
	 * Транзитная планета
	 */
	private Planet planet;
	/**
	 * Натальная небесная точка (планета|дом)
	 */
	private SkyPoint skyPoint;

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

	public Ingress(Planet planet, SkyPoint skyPoint, Model object) {
		super();
		this.planet = planet;
		this.skyPoint = skyPoint;
		this.object = object;
	}

	public Ingress() {}

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

	public static String _STATIC = "MOTION_STATIC";
	public static String _DIRECT = "MOTION_DIRECT";
	public static String _RETRO = "MOTION_RETRO";
	public static String _INGRESS = "SIGN_INGRESS";
	public static String _EXACT = "ASPECT_EXACT";
	public static String _SEPARATION = "ASPECT_SEPARATION";
	public static String _REPEAT = "ASPECT_REPEAT";
	public static String _EXACT_HOUSE = "HOUSE_EXACT";
	public static String _SEPARATION_HOUSE = "HOUSE_SEPARATION";
	public static String _REPEAT_HOUSE = "HOUSE_REPEAT";

	/**
	 * Возвращает используемые в отчёте ингрессии
	 * @return массив кодов
	 */
	public static String[] getKeys() {
		return new String[] {
			_EXACT, _EXACT_HOUSE,
			_SEPARATION, _SEPARATION_HOUSE,
			_RETRO, _DIRECT,
			_REPEAT, _REPEAT_HOUSE
		};
	}
}
