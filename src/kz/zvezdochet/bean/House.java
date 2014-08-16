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
}
