package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.Dictionary;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.core.util.CalcUtil;
import kz.zvezdochet.core.util.NumberUtil;
import kz.zvezdochet.service.AspectService;

/**
 * Аспект (угол между небесными точками)
 * @author Natalie Didenko
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
	 * Идентификатор типа аспекта
	 */
	private long typeid;
	/**
	 * Тип аспекта
	 */
	private AspectType type;
	/**
	 * Признак основного аспекта в своей категории
	 */
	private boolean main;

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

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
	 * Проверка, эквивалентно ли значение аспекту без учета орбисов.
	 * Если аспект не дробный, то сравниваем только целые части
	 * @param d значение
	 * @return true - значение эквивалентно значению аспекта
	 */
	public boolean isExact(double d) {
        return (exact)
        	? value == NumberUtil.round(d, 2)
            : value == CalcUtil.trunc(d);
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

	public long getTypeid() {
		return typeid;
	}

	public void setTypeid(long typeid) {
		this.typeid = typeid;
	}

	/**
	 * Планета, для которой предназначен аспект
	 */
	private long planetid;

	public long getPlanetid() {
		return planetid;
	}

	public void setPlanetid(long planetid) {
		this.planetid = planetid;
	}

	/**
	 * Очки
	 */
	private double points = 0.0;

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}

	/**
	 * Символ
	 */
	private String symbol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}	

	/**
	 * Признак аспекта, транзиты которого должны рассматриваться без орбиса
	 */
	private boolean exact = false;

	public boolean isExact() {
		return exact;
	}

	public void setExact(boolean exact) {
		this.exact = exact;
	}

	/**
	 * Проверка, является ли аспект аппликацией
	 * @param val значение реального аспекта между объектами
	 * @return true|false аппликация|сепарация
	 */
	public boolean isApplication(double val) {
	    return val <= value && getFloor() <= val;
	}

	/**
	 * Признак аспекта, существенного для прогноза:
	 * соединение, квадратура, трин, оппозиция
	 */
	private boolean strong;

	public boolean isStrong() {
		return strong;
	}

	public void setStrong(boolean strong) {
		this.strong = strong;
	}

	/**
	 * Проверка, является ли аспект аппликацией с орбисом в размере не более 1°
	 * @param val значение реального аспекта между объектами
	 * @return true|false аппликация|сепарация
	 */
	public boolean isApproximation(double val) {
	    return val < value && value - 1 < val;
	}

	public Aspect(AspectType type) {
		super();
		this.type = type;
	}
}
