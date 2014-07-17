package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Reference;
import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.service.PlaceService;

/**
 * Класс, представляющий Местонахождение
 * @author Nataly Didenko
 *
 * @see Reference Справочник
 */
public class Place extends Reference {
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
	
	public ReferenceService getService() {
		return new PlaceService();
	}
}
