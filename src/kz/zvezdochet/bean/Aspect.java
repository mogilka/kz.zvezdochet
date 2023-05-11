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
	 * Максимальный орбис
	 */
	private double orbis = 0.0;
	/**
	 * Минимальный орбис
	 */
	private double orbis_min = 0.0;
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
	 * Проверка, является ли указанное значение аспектом в пределах его орбисов
	 * @param d угол между объектами
	 * @return true - значение находится в диапазоне аспекта
	 */
	public boolean isAspect(double d) {
		double val = value > 0 ? d : 360 + d;
		return ((getFloor(0) <= val) && (val <= getCeiling(0)));
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
	 * @param orb орбис транзита планеты
	 * @return максимальное значение аспекта
	 */
	public double getCeiling(double orb) {
		double res = (orb > 0) ? orb : orbis;
		double val = value > 0 ? value : 360;
		return val + res;
	}

	/**
	 * Вычисление минимального значения аспекта. 
	 * Кроме соединения, все аспекты больше орбисов, поэтому берём значение аспекта как есть,
	 * а для соединения добавляем 360 градусов
	 * @param orb орбис транзита планеты
	 * @return минимальное значение аспекта
	 */
	public double getFloor(double orb) {
		double res = (orb > 0) ? orb : orbis;
		double val = value > 0 ? value : 360;
		return val - res;
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
	 * @param angle угол между объектами
	 * @return true|false аппликация|сепарация
	 */
	public boolean isApplication(double angle) {
		double aval = value > 0 ? value : 360;
		double val = value > 0 ? angle : 360 - angle;
	    return val < aval && getFloor(0) <= val;
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
	 * @param angle угол между объектами
	 * @return true|false аппликация|сепарация
	 */
	public boolean isApproximation(double angle) {
		double val = value > 0 ? angle : 360 + angle;
		return ((getFloor(1) <= val) && (val <= getCeiling(1)));
	}

	public Aspect(AspectType type) {
		super();
		this.type = type;
	}

	/**
	 * Проверка, эквивалентно ли значение аспекту с учётом транзитного орбиса минорной планеты
	 * @param angle угол транзита
	 * @param orbis орбис планеты
	 * @return true - значение эквивалентно значению аспекта с учётом орбисов планеты
	 */
	public boolean isTransit(double angle, double orbis) {
	    if (orbis < 4 && !isExact(angle))
	        return false;
        double res = Math.abs(angle - value);
        return res <= orbis;
	}

	public double getMinOrbis() {
		return orbis_min;
	}

	public void setMinOrbis(double orbis_min) {
		this.orbis_min = orbis_min;
	}

	/**
	 * Проверка, эквивалентно ли значение аспекту с учётом транзитного орбиса мажорной планеты
	 * @param angle угол транзита
	 * @param orbis орбис планеты
	 * @return true - значение эквивалентно значению аспекта с учётом орбисов планеты
	 */
	public boolean isMajorTransit(double angle, double orbis) {
//		if (angle < 6 && 0.5 == orbis)
//			System.out.println();
//		boolean exact = isExact(angle);
//	    boolean conj = code.equals("CONJUNCTION");
        double res = Math.abs(angle - value);
        return orbis >= res;
	}

	/**
	 * Проверка является ли аспект сильным
	 * @return true - основной аспект из числа нейтральных, гармоничных и негармоничных
	 */
	public boolean isMajor() {
		return id < 6;
	}
}
