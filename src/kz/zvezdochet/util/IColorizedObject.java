package kz.zvezdochet.util;

import org.eclipse.swt.graphics.Color;

/**
 * Интерфейс объекта, имеющего цвет
 * @author Nataly
 *
 */
public interface IColorizedObject {
	/**
	 * Метод, возвращающий цвет объекта	
	 * @return цвет
	 */
	public Color getColor();
	/**
	 * Инициализация цвета объекта
	 * @param color цвет
	 */
	public void setColor(Color color);
}
