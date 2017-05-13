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
     * Наименование
     */
    private String name;
    
    /**
     * Обозначение
     */
    private String designation;
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getLinkName() {
		return linkName;
	}

	public void setLinkName(String linkName) {
		this.linkName = linkName;
	}

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
	 * Признак куспида
	 */
	private boolean main = false;

	public void setMain(boolean main) {
		this.main = main;
	}

	/**
	 * Проверка, является ли дом основным
	 * @return <i>true</i> - если дом относится к главным куспидам,
	 * <i>false</i> - если дом относится к триплицетам
	 */
	public boolean isMain() {
  		return main;
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

	/**
	 * Признак дома, позиция которого в знаке Зодиака учитывается при экспорте гороскопа
	 */
	private boolean exportOnSign = false;

	public boolean isExportOnSign() {
		return exportOnSign;
	}

	public void setExportOnSign(boolean exportOnSign) {
		this.exportOnSign = exportOnSign;
	}

	public static Long[] getSportSet() {
		return new Long[] {142L,143L,144L,147L,148L,158L,161L,162L,163L,169L,176L};
	}

	/**
	 * Стихия дома
	 */
	private Element element;

	public Element getElement() {
		return element;
	}

	public void setElement(Element element) {
		this.element = element;
	}

	/**
	 * Описание для этапа жизни
	 */
	private String stage;

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}
}
