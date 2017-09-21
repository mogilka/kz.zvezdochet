package kz.zvezdochet.bean;

import kz.zvezdochet.core.bean.DiagramObject;
import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.SignService;

/**
 * Знак Зодиака
 * @author Nataly Didenko
 */
public class Sign extends DiagramObject {
	private static final long serialVersionUID = 70695380750415678L;
    /**
     * Порядковый номер
     */
    protected int number;
	/**
     * Начальный градус в эпоху 1 тысячелетия
     */
    private double i0;
	/**
     * Конечный градус в эпоху 1 тысячелетия
     */
    private double f0;
	/**
     * Начальный градус в эпоху 2 тысячелетия
     */
    private double i1000;
	/**
     * Конечный градус в эпоху 2 тысячелетия
     */
    private double f1000;
	/**
     * Начальный градус в эпоху 3 тысячелетия
     */
    private double i2000;
	/**
     * Конечный градус в эпоху 3 тысячелетия
     */
    private double f2000;
	/**
     * Начальный градус в эпоху 4 тысячелетия
     */
    private double i3000;
	/**
     * Конечный градус в эпоху 4 тысячелетия
     */
    private double f3000;

	/**
	 * Идентификатор стихии
	 */
	private long elementId;
	/**
	 * Идентификатор Инь-Ян
	 */
	private long yinyangId;
	/**
	 * Идентификатор креста
	 */
	private long crossId;
	/**
	 * Идентификатор квадрата
	 */
	private long squareId;
	/**
	 * Идентификатор зоны
	 */
	private long zoneId;
	/**
	 * Идентификатор вертикальной полусферы
	 */
	private long verticalHalfSphereId;
	/**
	 * Идентификатор горизонтальной полусферы
	 */
	private long horizontalalHalfSphereId;

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public double getI0() {
		return i0;
	}

	public void setI0(double initialPoint) {
		this.i0 = initialPoint;
	}

	public DictionaryService getService() {
		return new SignService();
	}

	public long getYinyangId() {
		return yinyangId;
	}

	public void setYinyangId(long yinyangId) {
		this.yinyangId = yinyangId;
	}

	public long getCrossId() {
		return crossId;
	}

	public void setCrossId(long crossId) {
		this.crossId = crossId;
	}

	public long getSquareId() {
		return squareId;
	}

	public void setSquareId(long squareId) {
		this.squareId = squareId;
	}

	public long getZoneId() {
		return zoneId;
	}

	public void setZoneId(long zoneId) {
		this.zoneId = zoneId;
	}

	public long getVerticalHalfSphereId() {
		return verticalHalfSphereId;
	}

	public void setVerticalHalfSphereId(long verticalHalfSphereId) {
		this.verticalHalfSphereId = verticalHalfSphereId;
	}

	public long getHorizontalalHalfSphereId() {
		return horizontalalHalfSphereId;
	}

	public void setHorizontalalHalfSphereId(long horizontalalHalfSphereId) {
		this.horizontalalHalfSphereId = horizontalalHalfSphereId;
	}

	/**
	 * Поиск противоположного знака Зодиака
	 * @param id идентификатор знака Зодиака
	 * @return массив идентификаторов противоположных знаков Зодиака
	 */
	public static int[] getOpposite(int id) {
		switch (id) {
			case 1: return new int[] {8};
			case 3: return new int[] {9,10};
			case 4: return new int[] {11};
			case 5: return new int[] {12};
			case 6: return new int[] {13};
			case 7: return new int[] {14};
			case 8: return new int[] {1};
			case 9: return new int[] {3};
			case 10: return new int[] {3};
			case 11: return new int[] {4};
			case 12: return new int[] {5};
			case 13: return new int[] {6};
			case 14: return new int[] {7};
		}
		return null;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	public double getF0() {
		return f0;
	}

	public void setF0(double f0) {
		this.f0 = f0;
	}

	public double getI1000() {
		return i1000;
	}

	public void setI1000(double i1000) {
		this.i1000 = i1000;
	}

	public double getF1000() {
		return f1000;
	}

	public void setF1000(double f1000) {
		this.f1000 = f1000;
	}

	public double getI2000() {
		return i2000;
	}

	public void setI2000(double i2000) {
		this.i2000 = i2000;
	}

	public double getF2000() {
		return f2000;
	}

	public void setF2000(double f2000) {
		this.f2000 = f2000;
	}

	public double getI3000() {
		return i3000;
	}

	public void setI3000(double i3000) {
		this.i3000 = i3000;
	}

	public double getF3000() {
		return f3000;
	}

	public void setF3000(double f3000) {
		this.f3000 = f3000;
	}

	/**
	 * HTML-символ знака Зодиака
	 */
	private String symbol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Стихия знака
	 */
	private Element element;

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * Краткое описание
	 */
	private String shortname;

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	/**
	 * Счастливые числа
	 */
	private String numbers;

	public String getNumbers() {
		return numbers;
	}

	public void setNumbers(String numbers) {
		this.numbers = numbers;
	}

	/**
	 * Критичный возраст
	 */
	private String years;

	public String getYears() {
		return years;
	}

	public void setYears(String years) {
		this.years = years;
	}

	/**
	 * Благоприятные цвета
	 */
	private String colors;
	/**
	 * Неблагоприятные цвета
	 */
	private String anticolors;

	public String getColors() {
		return colors;
	}

	public void setColors(String colors) {
		this.colors = colors;
	}

	public String getAnticolors() {
		return anticolors;
	}

	public void setAnticolors(String anticolors) {
		this.anticolors = anticolors;
	}

	/**
	 * Благоприятные дни недели
	 */
	private String weekdays;

	public String getWeekdays() {
		return weekdays;
	}

	public void setWeekdays(String weekdays) {
		this.weekdays = weekdays;
	}

	/**
	 * Девиз
	 */
	private String slogan;

	public String getSlogan() {
		return slogan;
	}

	public void setSlogan(String slogan) {
		this.slogan = slogan;
	}

	/**
	 * Талисман
	 */
	private String talisman;
	/**
	 * Амулет
	 */
	private String amulet;
	/**
	 * Благоприятные камни
	 */
	private String jewel;
	/**
	 * Благоприятные минералы
	 */
	private String mineral;
	/**
	 * Благоприятные металлы
	 */
	private String metal;
	/**
	 * Благоприятные растения
	 */
	private String flowers;

	public String getTalisman() {
		return talisman;
	}

	public void setTalisman(String talisman) {
		this.talisman = talisman;
	}

	public String getAmulet() {
		return amulet;
	}

	public void setAmulet(String amulet) {
		this.amulet = amulet;
	}

	public String getJewel() {
		return jewel;
	}

	public void setJewel(String jewel) {
		this.jewel = jewel;
	}

	public String getMineral() {
		return mineral;
	}

	public void setMineral(String mineral) {
		this.mineral = mineral;
	}

	public String getMetal() {
		return metal;
	}

	public void setMetal(String metal) {
		this.metal = metal;
	}

	public String getFlowers() {
		return flowers;
	}

	public void setFlowers(String flowers) {
		this.flowers = flowers;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Поиск знаков Зодиака родственной стихии
	 * @param integer $id идентификатор знака Зодиака
	 * @return array массив идентификаторов родственных знаков Зодиака
	 */
	public static int[] getByElement(int id) {
		switch (id) {
			case 1: return new int[] {6,10,11};
			case 3: return new int[] {7,12};
			case 4: return new int[] {8,13};
			case 5: return new int[] {9,14};
			case 6: return new int[] {1,10,11};
			case 7: return new int[] {3,12};
			case 8: return new int[] {4,13};
			case 9: return new int[] {5,14};
			case 10: return new int[] {1,6,11};
			case 11: return new int[] {1,6,11};
			case 12: return new int[] {3,7};
			case 13: return new int[] {4,8};
			case 14: return new int[] {5,9};
		}
		return null;
	}

	/**
	 * Ключевые слова
	 */
	private String keyword;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
}
