package kz.zvezdochet.util;

import java.util.Comparator;

import kz.zvezdochet.bean.SkyPoint;


/**
 * Класс для сортировки списка небесных точек
 * по возрастанию их координат
 * @author Nataly Didenko
 *
 */
public class SkyPointComparator implements Comparator<SkyPoint> {

	@Override
	public int compare(SkyPoint point1, SkyPoint point2) {
		return Double.compare(Math.abs(point1.getCoord()), Math.abs(point2.getCoord()));
	}

}
