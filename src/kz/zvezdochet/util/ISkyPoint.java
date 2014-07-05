package kz.zvezdochet.util;

/**
 * Интерфейс точки небесной сферы
 * @author Nataly
 *
 */
public interface ISkyPoint extends IColorizedObject {
	/**
	 * Метод, возвращающий координату небесной точки	
	 * @return координата (угол в пределах 360 градусов)
	 */
	public double getCoord();
	/**
	 * Инициализация координаты небесной точки
	 * @param coord значение
	 */
	public void setCoord(double coord);
}
