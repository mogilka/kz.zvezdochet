package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.service.AspectService;

/**
 * Аспект (угол между небесными точками)
 * @author Nataly Didenko
 */
public class Aspect extends Dictionary {
	private static final long serialVersionUID = 4389850164699356397L;

	/**
	 * Значение
	 */
	private double value = 0.0;
	
	/**
	 * Орбис
	 */
	private double orbis = 0.0;
	
	/**
	 * Тип аспекта
	 */
	private AspectType type;
	
	public Aspect() {
		super();
	}
	
	public AspectType getType() {
		return type;
	}

	public void setType(AspectType type) {
		this.type = type;
	}

	/**
	 * Проверка, является ли указанное значение аспектом
	 * @param d значение
	 * @return true - значение находится в диапазоне аспекта
	 */
	public boolean isAspect(double d) {
		return ((getFloor() <= d) && (d <= getCeiling()));
	}

	/**
	 * Проверка, является ли указанное значение точным аспектом
	 * @param d значение
	 * @return true - значение эквивалентно значению аспекта
	 */
	public boolean isExactAspect(double d) {
		return value == d; 
	}

	/**
	 * Проверка, эквивалентно ли указанное значение целой части аспекта без учета орбисов
	 * @param d значение
	 * @return true - значение эквивалентно целому значению аспекта
	 */
	public boolean isExactTruncAspect(double d) {
		return value == CalcUtil.trunc(d); 
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getOrbis() {
		return orbis;
	}

	public void setOrbis(double orbis) {
		this.orbis = orbis;
	}

	/**
	 * Вычисление максимального значения аспекта
	 * @return
	 * @todo учитывать нулевой градус
	 */
	public double getCeiling() {
		return value + orbis;
	}

	/**
	 * Вычисление минимального значения аспекта
	 * @return
	 * @todo учитывать нулевой градус
	 */
	public double getFloor() {
		return value - orbis;
	}

	public DictionaryService getService() {
		return new AspectService();
	}

	@Override
	public String toString() {
		return name + " " + value;
	}
}
