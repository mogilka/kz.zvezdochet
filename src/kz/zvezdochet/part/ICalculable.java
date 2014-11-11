package kz.zvezdochet.part;

/**
 * Интерфейс для обработки результатов расчёта
 * @author Nataly Didenko
 *
 */
public interface ICalculable {
	/**
	 * Обработка результатов расчёта
	 * @param obj объект для вычислительных целей
	 */
	public void onCalc(Object obj);
}
