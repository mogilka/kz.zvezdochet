package kz.zvezdochet.bean;

import kz.zvezdochet.core.service.ReferenceService;
import kz.zvezdochet.service.SignService;

/**
 * Класс, представляющий Знак Зодиака
 * @author Nataly Didenko
 * 
 * @see SkyPoint Точка небесной сферы
 */
public class Sign extends SkyPoint {
	private static final long serialVersionUID = 70695380750415678L;

	/**
     * Начальная точка небесной области
     */
    private double initialPoint;

	public double getInitialPoint() {
		return initialPoint;
	}

	public void setInitialPoint(double initialPoint) {
		this.initialPoint = initialPoint;
	}

	public ReferenceService getService() {
		return new SignService();
	}
}
