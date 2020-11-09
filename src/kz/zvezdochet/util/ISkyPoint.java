package kz.zvezdochet.util;

import kz.zvezdochet.core.bean.IColorizedObject;

/**
 * Интерфейс точки небесной сферы
 * @author Natalie Didenko
 *
 */
public interface ISkyPoint extends IColorizedObject {
	/**
	 * Метод, возвращающий координату небесной точки	
	 * @return координата (угол в пределах 360 градусов)
	 */
	public double getLongitude();
	/**
	 * Инициализация координаты небесной точки
	 * @param coord значение
	 */
	public void setLongitude(double coord);
}
