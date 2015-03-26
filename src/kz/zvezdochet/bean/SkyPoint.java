package kz.zvezdochet.bean;

import java.util.List;
import java.util.Map;

import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.bean.Model;
import kz.zvezdochet.core.service.DataAccessException;
import kz.zvezdochet.service.SignService;
import kz.zvezdochet.util.ISkyPoint;


/**
 * Точка Небесной сферы
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
    protected int number;

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
	protected Map<String, Integer> aspectCountMap;

	@Override
	public String toString() {
		return name + " " + coord;
	}

	/**
	 * Определение знака, в котором находится объект
	 * @param point координата объекта
	 * @param year год
	 * @throws DataAccessException 
	 */
	public static Sign getSign(double point, int year) throws DataAccessException {
		List<Model> signs = new SignService().getList();
		for (Model model : signs) {
			Sign sign = (Sign)model;
			if (year < 1000) {
				if (point >= sign.getI0() && point < sign.getF0()) 
					return sign;
			} else if (year < 2000) {
				if (point >= sign.getI1000() && point < sign.getF1000()) 
					return sign;
			} else if (year < 3000) {
				if (point >= sign.getI2000() && point < sign.getF2000()) 
					return sign;
			} else if (year < 4000) {
				if (point >= sign.getI3000() && point < sign.getF3000()) 
					return sign;
			}
		}
		return null;
	}

	/**
	 * Определение дома, в котором находится объект
	 * @param house1 координата дома
	 * @param house2 координата следующего дома
	 * @param coord координата объекта
	 */ 
	public static boolean getHouse(double house1, double house2, double coord) {
		coord = Math.abs(coord);
		//если границы домов находятся по разные стороны нуля
		if (house1 > 200 & house2 < 160) {
			//если градус объекта находится по другую сторону
			//от нуля относительно второй границы,
			//увеличиваем эту границу на 2*Pi
			if (coord > 200)
				house2 += 360;
			else if (coord < 160) {
				//если градус планеты меньше 160,
				//увеличиваем его, а также вторую границу на 2*Pi
		       coord += 360;
		       house2 += 360;
			}
		}
		//если же границы находятся по одну сторону от нуля,
		//оставляем всё как есть
		
		return house1 <= coord && coord <= house2;
	}
}
