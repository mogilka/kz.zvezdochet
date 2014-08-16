package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.PlaceService;

/**
 * Местонахождение
 * @author Nataly Didenko
 */
public class Place extends Dictionary {
	private static final long serialVersionUID = 7176275971217317131L;

	/**
	 * Долгота
	 */
	private double longitude;
	/**
	 * Широта
	 */
	private double latitude;
	/**
	 * Разница с Гринвичем
	 */
	private double greenwich;
	
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getGreenwich() {
		return greenwich;
	}
	public void setGreenwich(double greenwich) {
		this.greenwich = greenwich;
	}
	
	public DictionaryService getService() {
		return new PlaceService();
	}
}
