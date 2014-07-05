package kz.zvezdochet.util;

/**
 * Интерфейс объекта, отображаемого в диаграмме
 * @author Nataly
 *
 */
public interface IDiagramObject {
	/**
	 * Метод, возвращающий наименование объекта в диаграмме	
	 * @return наименование
	 */
	public String getDiaName();
	/**
	 * Инициализация наименования объекта для диаграммы
	 * @param diaName наименование
	 */
	public void setDiaName(String diaName);
}
