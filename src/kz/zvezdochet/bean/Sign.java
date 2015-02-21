package kz.zvezdochet.bean;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.SignService;

/**
 * Знак Зодиака
 * @author Nataly Didenko
 */
public class Sign extends SkyPoint {
	private static final long serialVersionUID = 70695380750415678L;

	/**
     * Начальная точка небесной области
     */
    private double initialPoint;
	/**
	 * Идентификатор стихии
	 */
	private long elementId;
	/**
	 * Идентификатор Инь-Ян
	 */
	private long yinyangId;
	/**
	 * Идентификатор креста
	 */
	private long crossId;
	/**
	 * Идентификатор квадрата
	 */
	private long squareId;
	/**
	 * Идентификатор зоны
	 */
	private long zoneId;
	/**
	 * Идентификатор вертикальной полусферы
	 */
	private long verticalHalfSphereId;
	/**
	 * Идентификатор горизонтальной полусферы
	 */
	private long horizontalalHalfSphereId;

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public double getInitialPoint() {
		return initialPoint;
	}

	public void setInitialPoint(double initialPoint) {
		this.initialPoint = initialPoint;
	}

	public DictionaryService getService() {
		return new SignService();
	}

	public long getYinyangId() {
		return yinyangId;
	}

	public void setYinyangId(long yinyangId) {
		this.yinyangId = yinyangId;
	}

	public long getCrossId() {
		return crossId;
	}

	public void setCrossId(long crossId) {
		this.crossId = crossId;
	}

	public long getSquareId() {
		return squareId;
	}

	public void setSquareId(long squareId) {
		this.squareId = squareId;
	}

	public long getZoneId() {
		return zoneId;
	}

	public void setZoneId(long zoneId) {
		this.zoneId = zoneId;
	}

	public long getVerticalHalfSphereId() {
		return verticalHalfSphereId;
	}

	public void setVerticalHalfSphereId(long verticalHalfSphereId) {
		this.verticalHalfSphereId = verticalHalfSphereId;
	}

	public long getHorizontalalHalfSphereId() {
		return horizontalalHalfSphereId;
	}

	public void setHorizontalalHalfSphereId(long horizontalalHalfSphereId) {
		this.horizontalalHalfSphereId = horizontalalHalfSphereId;
	}

	/**
	 * Поиск противоположного знака Зодиака
	 * @param id идентификатор знака Зодиака
	 * @return массив идентификаторов противоположных знаков Зодиака
	 */
	public static int[] getOpposite(int id) {
		switch (id) {
			case 1: return new int[] {8};
			case 3: return new int[] {9,10};
			case 4: return new int[] {11};
			case 5: return new int[] {12};
			case 6: return new int[] {13};
			case 7: return new int[] {14};
			case 8: return new int[] {1};
			case 9: return new int[] {3};
			case 10: return new int[] {3};
			case 11: return new int[] {4};
			case 12: return new int[] {5};
			case 13: return new int[] {6};
			case 14: return new int[] {7};
		}
		return null;
	}
}
