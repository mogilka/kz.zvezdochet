package kz.zvezdochet.bean;

import java.util.Map;

import kz.zvezdochet.util.DiagramObject;
import kz.zvezdochet.util.ISkyPoint;


/**
 * Класс, представляющий Точку Небесной сферы
 * @author Nataly Didenko
 *
 */
public abstract class SkyPoint extends DiagramObject implements ISkyPoint {
	private static final long serialVersionUID = 6159825158439746993L;

	/**
	 * Координата
	 */
    protected double coord = 0.0;

    /**
     * Порядковый номер
     */
    private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public double getCoord() {
		return coord;
	}

	public void setCoord(double coord) {
		this.coord = coord;
	}

	public void setAspectCountMap(Map<String, Integer> aspectMap) {
		this.aspectCountMap = aspectMap;
	}

	public Map<String, Integer> getAspectCountMap() {
		return aspectCountMap;
	}

	/**
	 * Карта статистики типов аспектов для небесной точки
	 */
	private Map<String, Integer> aspectCountMap;
}
