package kz.zvezdochet.bean;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.HouseService;

/**
 * Астрологический дом
 * @author Nataly Didenko
 *
 */
public class House extends SkyPoint {
	private static final long serialVersionUID = 243009757276043770L;

    /**
     * Знак, в котором находится куспид дома
     */
    private Sign sign;
    
    /**
     * Словосочетание, используемое для объекта,
     * который расположен в доме
     */
    private String combination;
    
    /**
     * Краткое наименование
     */
    private String shortName;
    
    /**
     * Обозначение
     */
    private String designation;
    
    public String getCombination() {
		return combination;
	}

	public void setCombination(String combination) {
		this.combination = combination;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String headerName) {
		this.headerName = headerName;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

	/**
     * Краткое наименование, используемое в html-странице
     */
    private String headerName;
    
    /**
     * Обозначение, используемое в html-ссылке
     */
    private String linkName;
    
	public House() {}

	public House(String name) {
		setName(name);
	}
	
	public Sign getSign() {
		return sign;
	}
	public void setSign(Sign sign) {
		this.sign = sign;
	}

	/**
	 * Проверка, является ли дом основным
	 * @return <i>true</i> - если дом относится к главным куспидам
	 */
	public boolean isMain() {
  		int[] hmain = {1,4,7,10,13,16,19,22,25,28,31,34};
  		for (int i : hmain)
  			if (getNumber() == i) return true;
  		return false;
	}

	public DictionaryService getService() {
		return new HouseService();
	}

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

	public House(House house) {
		id = house.id;
		name = house.name;
		code = house.code;
		coord = house.coord;
		number = house.number;
		designation = house.designation;
	}
}
