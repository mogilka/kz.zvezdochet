package kz.zvezdochet.bean;

import java.util.ArrayList;
import java.util.List;

import kz.zvezdochet.core.service.DictionaryService;
import kz.zvezdochet.service.HouseService;

/**
 * Астрологический дом
 * @author Natalie Didenko
 *
 */
public class House extends SkyPoint {
	private static final long serialVersionUID = 243009757276043770L;

    /**
     * Знак, в котором находится куспид дома
     */
    private Sign sign;
    
    /**
     * Обозначение
     */
    private String designation;
    
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String linkName) {
		this.category = linkName;
	}

	/**
     * Категории для синастрии
     */
    private String category;
    
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
		longitude = house.longitude;
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
		return new Long[] {142L,143L,144L,147L,148L,153L,154L,158L,161L,162L,163L,164L,166L,169L,170L,171L,176L,177L};
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

	/**
	 * Описание для позитивного прогноза
	 */
	private String positive;
	/**
	 * Описание для негативного прогноза
	 */
	private String negative;

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	public String getNegative() {
		return negative;
	}

	public void setNegative(String negative) {
		this.negative = negative;
	}

    /**
     * Признак Лилит в доме
     */
    private boolean lilithed;
	/**
	 * Признак Селены в доме
	 */
	private boolean selened;

	public boolean isLilithed() {
		return lilithed;
	}

	public void setLilithed() {
		this.lilithed = true;
	}

	public boolean isSelened() {
		return selened;
	}

	public void setSelened() {
		this.selened = true;
	}

	/**
	 * Планеты в доме
	 */
	private List<Planet> planets;

	public List<Planet> getPlanets() {
		if (null == planets)
			planets = new ArrayList<>();
		return planets;
	}

	/**
	 * Описание дома для синастрии
	 */
	private String synastry;

	public String getSynastry() {
		return synastry;
	}

	public void setSynastry(String synastry) {
		this.synastry = synastry;
	}

	/**
	 * Поиск идентификатора противоположного дома
	 * @return идентификатор противостоящего дома
	 */
	public long getOppositeId() {
		long hid = 0;
		hid = (id > 159) ? id - 18 : id + 18;
		return (hid > 0) ? hid : 0;
	}

	/**
	 * Общее описание (применимо только к вершинам домов)
	 */
	private String general;

	public String getGeneral() {
		return general;
	}

	public void setGeneral(String general) {
		this.general = general;
	}

	/**
	 * Проверка является ли дом угловым
	 * @return
	 */
	public boolean isAngled() {
		return id == 142
			|| id == 151
			|| id == 160
			|| id == 169;
	}

	/**
	 * Предназначение по дому
	 */
	private String mission;

	public String getMission() {
		return mission;
	}

	public void setMission(String mission) {
		this.mission = mission;
	}
}
