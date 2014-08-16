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

	public double getInitialPoint() {
		return initialPoint;
	}

	public void setInitialPoint(double initialPoint) {
		this.initialPoint = initialPoint;
	}

	public DictionaryService getService() {
		return new SignService();
	}
}
